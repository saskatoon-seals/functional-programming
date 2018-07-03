package fpinscala.streamprocessing

import fpinscala.io.IO
import fpinscala.monads.Monad

trait Process[F[_], O] {
  import Process._
  
  //Primitive functions:
  def onHalt(f: Throwable => Process[F,O]): Process[F,O] = this match {
    case Halt(e) => Try(f(e))
    case Emit(h, t) => Emit(h, t.onHalt(f))
    case Await(req,recv) => Await(req, recv andThen (_.onHalt(f)))
  }
  
  /*
   * The general interpreter for exception based monads
   * 
   * This function is defined only if given a `MonadCatch[F]`. 
   * Defined for any Monad that deals with exceptions (throws and catches them) 
   * 
   * Unlike the simple `runLog` interpreter defined in the companion object
   * below, this is not tail recursive and responsibility for stack safety
   * is placed on the `Monad` instance.
   */
  def runLog(implicit F: MonadCatch[F]): F[IndexedSeq[O]] = {
    val E = java.util.concurrent.Executors.newFixedThreadPool(4)
    import fpinscala.io.Interpreter.unsafePerformIO
    
//    @annotation.tailrec -> why?
    def go(cur: Process[F,O], acc: IndexedSeq[O]): F[IndexedSeq[O]] =
      cur match {
        case Emit(h,t) => go(t, acc :+ h) 
        case Halt(End) => F.unit(acc)
        case Halt(err) => F.fail(err)
        case Await(req,recv) => F.flatMap (F.attempt(req)) { result => go(Try(recv(result)), acc) }
      }
    
    try go(this, IndexedSeq())
    finally E.shutdown
  }
  
  //Derived functions:
  def ++(p: => Process[F,O]): Process[F,O] = this.onHalt {
    case End => Try(p) // we consult `p` only on normal termination
    case err => Halt(err)
  }
  
  //derived because of using ++
  def flatMap[O2](f: O => Process[F,O2]): Process[F,O2] = this match {
    case Halt(err) => Halt(err)
    case Emit(o, t) => Try(f(o)) ++ t.flatMap(f)
    case Await(req,recv) => Await(req, recv andThen (_ flatMap f))
  }
  
  //Helper functions:
  def Try[F[_],O](p: => Process[F,O]): Process[F,O] =
    try p
    catch { case e: Throwable => Halt(e) }
}

trait MonadCatch[F[_]] extends Monad[F] {
  def attempt[A](a: F[A]): F[Either[Throwable, A]]
  def fail[A](t: Throwable): F[A]
}

object Process {
  case class Await[F[_],A,O](
    req: F[A],
    recv: Either[Throwable,A] => Process[F,O]) extends Process[F,O]

  case class Emit[F[_],O](
    head: O,
    tail: Process[F,O]) extends Process[F,O]

  case class Halt[F[_],O](err: Throwable) extends Process[F,O]
  
  /* Special exception indicating normal termination */
  case object End extends Exception

  /* Special exception indicating forceful termination */
  case object Kill extends Exception
  
  //currying to make better use of type inference
  def await[F[_],A,O](req: F[A])(recv: Either[Throwable,A] => Process[F,O]): Process[F,O] = 
    Await(req, recv) 
  
  /*
   * The interpreter of an I/O Process that collects all the values emitted.
   * 
   * Here is a simple tail recursive function to collect all the
   * output of a `Process[IO,O]`. Notice we are using the fact
   * that `IO` can be `run` to produce either a result or an
   * exception.
   */
  def runLog[O](src: Process[IO,O]): IO[IndexedSeq[O]] = IO {
    val E = java.util.concurrent.Executors.newFixedThreadPool(4)
    import fpinscala.io.Interpreter.unsafePerformIO
    
    @annotation.tailrec
    def go(cur: Process[IO,O], acc: IndexedSeq[O]): IndexedSeq[O] =
      cur match {
        case Emit(h,t) => go(t, acc :+ h) //:+ copy of the sequence with the "h" appended
        case Halt(End) => acc
        case Halt(err) => throw err
        
        /*
         * 1. req is the request that needs to be executed by this interpreter (runLog)
         * 2. runLog also defines how to execute the recv callback
         */
        case Await(req,recv) =>
          val next: Process[IO, O] =
            /*
             * 1. Executes a request that performs a side-effect -> can fail
             * 2. Wraps it into a Right(..value..)
             * 3. Calls the recv callback with the value
             */
            try recv(Right(unsafePerformIO(req)(E)))
            /*
             * 1. Executing the unsafePerformIO threw an exception
             * 2. Executes recv - receive value callback
             *  a) can fall back to another process 
             *  b) can clean up resources
             */
            catch { case err: Throwable => recv(Left(err)) } //catches all exceptions!
          go(next, acc)
      }
    
    //try-with-resources without catching exceptions
    try go(src, IndexedSeq())
    finally E.shutdown
  }
 
  /*
   * Single value evaluation
   * 
   * Promotes F[A] to a Process that emits only the result of F[A]
   * idiom of using await to "evaluate"
   * 
   * An example of F[A] is IO[A]
   * 
   * There's no need to know anything about F
   */
  def eval[F[_],A](fa: F[A]): Process[F,A] = {
    await(fa)(result => result match {
      case Right(a) => Emit(a, Halt(End))
      case Left(err) => Halt(err)
    })   
  }
  
  //Emits no values (evaluates only for the effect - consumer interface in Java)
  def eval_[F[_],A](fa: F[A]): Process[F,A] = {
    await(fa)(result => result match {
      case Right(_) => Halt(End)
      case Left(err) => Halt(err)
    })
  }
  
  //"flatten"
  def join[F[_],O](p: Process[F, Process[F,O]]): Process[F,O] = 
    p.flatMap(identity)   
}