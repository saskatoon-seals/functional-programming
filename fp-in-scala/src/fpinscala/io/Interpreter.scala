package fpinscala.io

import scala.annotation.tailrec

import fpinscala.monads.Monad

object Interpreter {
  @tailrec
  def run[A](io: IO[A]): A = io match {
    case IO.Return(a) => a //no side-effect
    case IO.Suspend(effect) => effect() //executes the effect (has a side-effect)
    case IO.FlatMap(x, f) => x match {
      case IO.Return(a) => run(f(a))
      case IO.Suspend(effect) => run(f(effect()))
      case IO.FlatMap(y, g) => run(y flatMap (a => g(a) flatMap f))
    }
  }
  
  @tailrec
  def runTrampoline[A](free: Free[Function0,A]): A = free match {
    case Free.Return(a) => a
    case Free.Suspend(f) => f()()
    case Free.FlatMap(x, f) => x match {
      case Free.Return(a) => runTrampoline(f(a))
      case Free.Suspend(effect) => runTrampoline(f(effect()))
      case Free.FlatMap(y, g) => runTrampoline(y flatMap (a => g(a) flatMap f))
    }
  }
  
  @annotation.tailrec
  def step[F[_],A](a: Free[F,A]): Free[F,A] = a match {
    case Free.FlatMap(Free.FlatMap(x, f), g) => step(x flatMap (a => f(a) flatMap g))
    case Free.FlatMap(Free.Return(x), f) => step(f(x))
    case _ => a
  }
  
//  @tailrec
  def runFree[F[_],A](free: Free[F,A])(implicit F: Monad[F]): F[A] = step(free) match {
    case Free.Return(a) => F.unit(a)
    case Free.Suspend(r) => r() //F.flatMap(r())(a => F.unit(a))
    case Free.FlatMap(Free.Suspend(r), f) => F.flatMap(r())(a => runFree(f(a))) 
    case _ => sys.error("can't happen!")
  }
  
  
  // To run an `IO`, we need an executor service.
  // The name we have chosen for this method, `unsafePerformIO`,
  // reflects that is is unsafe, i.e. that it has side effects,
  // and that it _performs_ the actual I/O.
  import java.util.concurrent.ExecutorService
  
  def unsafePerformIO[A](io: IO[A])(implicit E: ExecutorService): A =
    throw new UnsupportedOperationException
}