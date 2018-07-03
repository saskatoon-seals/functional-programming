package fpinscala.streamprocessing

import fpinscala.io.IO

sealed trait Process1[I, O] {
  import Process1._
  
  /*
   * A sample driver (for the Stream)
   * 
   * A `Process[I,O]` can be used to transform a `Stream[I]` to a `Stream[O]`.
   */
  def apply(s: Stream[I]): Stream[O] = this match {
    case Halt() => Stream()
    
    case Await(recv) => s match {
      case h #:: t => recv(Some(h))(t)
      case xs => recv(None)(xs) // Stream is empty
    }
    
    case Emit(h,t) => h #:: t(s) //cons
  }
  
  /*
 	 * `Process` definitions can often be expressed without explicit
   * recursion, by repeating some simpler `Process` forever.
   * 
   * Alternation between awaiting and emitting
   */
  def repeat: Process1[I,O] = {
    def go(p: Process1[I,O]): Process1[I,O] = p match {
      case Halt() => go(this) //restarts the process
      
      case Await(recv) => Await {
        case None => recv(None) //don't repeat - termination comes from the source
        case i => go(recv(i))
      }
      
      case Emit(h, t) => Emit(h, go(t))
    }
    
    go(this)
  }
  
  //-----------------------------------------combinators--------------------------------------------
  
  /*
   * Fuse (compose) operator
   * 
   * p1 |> p2 => fuses the processes (feeds output of p1 as an input into p2) 
   */
  def |> [O2](p2: Process1[O, O2]): Process1[I, O2] = p2 match {
    case Halt() => Halt()
    case Emit(h2, t2) => Emit(h2, this |> t2)
    case Await(recv2) => this match {
      case Halt() => Halt() |> recv2(None)
      case Emit(h1, t1) => t1 |> recv2(Some(h1))
      case Await(recv1) => Await(input => (recv1(input) |> p2))
    }
  }
  
  def map[O2](f: O => O2): Process1[I, O2] = 
    this |> lift(f)
    
  //product (the same input is fed into both processes)
  def zipWith[O2](p2: Process1[I,O2]): Process1[I, (O, O2)] = (this, p2) match {
    //by design if one of the processes terminates the resulting process terminates
    case (Halt(), _) => Halt()
    case (_, Halt()) => Halt()
    
    //both processes are ready
    case (Emit(h1, t1), Emit(h2, t2)) => Emit((h1, h2), t1 zipWith t2)
    
    //the same (shared) input must be feed to both processes
    case (Await(recv1), _) => Await(input => recv1(input) zipWith p2.feed(input))
    case (_, Await(recv2)) => Await(input => this.feed(input) zipWith recv2(input))
  }
    
  def feed(input: Option[I]): Process1[I,O] = this match {
    case Halt() => Halt()
    case Emit(h, t) => Emit(h, t.feed(input))
    case Await(recv) => recv(input) //the actual "feeding"
  }
    
  /*
   * Append (concatenate) processes
   * 
   * Returns a process that runs "this" until completion and then runs "p" with the remaining part 
   * of the input
   */
  def ++(p: => Process1[I,O]): Process1[I,O] = this match {
    case Halt() => p
    case Emit(h, t) => Emit(h, t ++ p)
    //this is the same as recv andThen (proc => proc ++ p) == recv andThen (_ ++ p)
    case Await(recv) => Await(input => recv(input) ++ p) 
  }
    
  def zipWithIndex: Process1[I, (O, Int)] = 
    this |> loop(0)((i, s) => ((i, s), s + 1))
}

/*
 * Indicates to the driver that the "head" should be emitted to the output stream
 * 
 * Emit(xs) == Emit(xs, Halt())
 */
case class Emit[I,O](head: O, tail: Process1[I,O] = Halt[I,O]()) extends Process1[I,O]

// Requests a value from the input stream (driver must pass next value to recv)
case class Await[I,O](recv: Option[I] => Process1[I,O]) extends Process1[I,O]

// Indicates to the driver that the input stream empty => STOP! 
case class Halt[I,O]() extends Process1[I,O]

object Process1 {
  def filter[I](p: I => Boolean): Process1[I,I] = Await[I,I] {
    case Some(i) if (p(i)) => Emit(i, filter(p)) 
    case _ => Halt()
  }.repeat
  
  /*
   * We can convert any function `f: I => O` to a `Process[I,O]`. We
   * simply `Await`, then `Emit` the value received, transformed by
   * `f`.
   */
  def liftOne[I,O](f: I => O): Process1[I,O] = Await {
    case Some(i) => Emit(f(i))
    case None => Halt()
  }
  
  def lift[I, O](f: I => O): Process1[I, O] = liftOne(f).repeat
  
  /**
   * A helper function to await an element or fall back to another process
   * if there is no input.
   */
  def await[I,O](f: I => Process1[I,O], fallback: Process1[I,O] = Halt[I,O]()): Process1[I,O] =
    Await[I,O] {
      case Some(i) => f(i)
      case None => fallback
  }
  
  //halts the process after it encounters the given number of elements
  def take[I](n: Int): Process1[I, I] = {
    if (n == 0) 
      Halt()
    else 
      await(i => Emit(i, take(n-1)))
  }
  
  def drop[I](n: Int): Process1[I, I] = {
    if (n == 0) 
      liftOne[I,I](identity).repeat //keeps echoing forever
    else
      await(_ => drop(n-1))
  }
  
  def takeWhile[I](p: I => Boolean): Process1[I,I] = await(i => 
    if (p(i)) 
      Emit(i, takeWhile(p)) 
    else 
      Halt()
  )
  
  def dropWhile[I](p: I => Boolean): Process1[I, I] = await(i =>
    if (p(i))
      dropWhile(p)
    else 
      Emit(i, liftOne[I,I](identity).repeat) //why wrapped inside Emit(..)?
  )
  
  /*
   * Counts the number of elements seen so far (scan-like function)
   * 
   * count(Stream("a", "b")) == Stream(1, 2)
   */
  def count[I]: Process1[I, Int] = {
    def go(count: Int): Process1[I, Int] =  
      await(i => Emit(count, go(count + 1)))  
      
    go(1)
  }
  
  def count1[I]: Process1[I, Int] = 
    loop(1)((_, count) => (count, count + 1)) 
  
  def runningAverage: Process1[Double, Double] = {
    def go(prev: Double, count: Int): Process1[Double, Double] =        
      await(i => {
        val next = prev + (i - prev)/count
        
        Emit(next, go(next, count + 1))
      })
    
    go(0.0, 1)
  }
  
  def loop[S,I,O](state: S)(f: (I,S) => (O,S)): Process1[I,O] = 
    await(input => {
      val (output, newState) = f(input, state)
      
      Emit(output, loop(newState)(f))
    })
    
  def sum[I](implicit n: Numeric[I]): Process1[I, I] = {
    loop(n.zero) { (i, sum: I) => 
      val newSum = n.plus(i, sum) 
      
      (newSum, newSum) 
    }  
  }

  //expressed in terms of sum and count (zipping 2 processes)
  def mean: Process1[Double, Double] = {
    (Process1.sum[Double] zipWith count) map ({
      case (x: Double, y: Int) => x / y
    })   
  }
  
  //halting and only yielding the final result
  def exists1[I](p: I => Boolean): Process1[I,Boolean] = await(
    input => {
      if (p(input))
        Emit(true)
      else
        exists1(p)
  })
 
  
  //halting and yielding all intermediate results
  def exists2[I](p: I => Boolean): Process1[I,Boolean] = await(input => {
    if (p(input))
      Emit(true)
    else
      Emit(false, exists2(p))
  })
  
  //not halting and yielding all intermediate results
  def exists3[I](p: I => Boolean): Process1[I,Boolean] = {
    def go(acc: Boolean): Process1[I,Boolean] = await (input => {
      if (acc || p(input))
        Emit(true, go(true))
      else 
        Emit(false, go(false))
    })  
    
    go(false)
  }
  
  def processFile[A,B](f: java.io.File, p: Process1[String, A], z: B)(g: (B, A) => B): IO[B] = IO {
    @annotation.tailrec
    def go(ss: Iterator[String], cur: Process1[String, A], acc: B): B =
      cur match {
        case Halt() => acc
        
        case Await(recv) =>
          val next = if (ss.hasNext) recv(Some(ss.next))
                     else recv(None)
          go(ss, next, acc)
        
        case Emit(h, t) => go(ss, t, g(acc, h))
      }
    
    val s = io.Source.fromFile(f)
    
    //try-with-resources
    try go(s.getLines, p, z)
    finally s.close
  }
}