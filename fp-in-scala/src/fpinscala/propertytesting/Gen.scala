package fpinscala.propertytesting

import fpinscala.purestate._;

/*
 * Gen is something that knows how to generate values of type A.
 * It uses randomness to generate these values. 
 * 
 * Gen is a type that wraps State action over random number generator.
 */
case class Gen[+A](next: State[RNG,A]) {
  def map[B](f: A => B): Gen[B] = 
    Gen(next.map(f))
    
  def map2[B,C](gen2: Gen[B])(f: (A, B) => C): Gen[C] = 
    Gen(next.map2(gen2.next)(f))
  
  def flatMap[B](f: A => Gen[B]): Gen[B] = 
    Gen(next flatMap { a => f(a).next })
    
  /**
   * Combines this generator with the genSize in such a way that it:
   *  - uses this generator to generate elements of type A
   *  - uses genSize generator to generate the size of list of elements A
   * 
   * @param size - size of a list of elements of type A
   */
  def lisfOfN(genSize: Gen[Int]): Gen[List[A]] =
    genSize.flatMap(size => {
      val stateActions = List.fill(size)(next)
    
      Gen(State.sequence(stateActions))  
    })
    
  //Returns a sized generator regardless of the size
  def unsized: SGen[A] = 
    SGen(_ => this)
}

object Gen {
  /*
   * Creates a generator that generates integers within the provided range 
   * [primitive]
   * 
   * @param start - starting range
   * @param stopExclusive - ending range
   * 
   * @return generator of integers within the provided range
   */
  def choose(start: Int, stopExclusive: Int): Gen[Int] = {
    //re-calculates x to lay on the interval [start, stop)
    def scaleToRange(x: Int): Int = {
      val y: Double = x / (Int.MaxValue + 1)
      
      start + ((stopExclusive - start) * y).toInt
    }
    
    def stateAction: State[RNG,Int] = 
      State(rng => {
        val (x, newRng) = rng.nextInt
    
        (scaleToRange(x), newRng)
      })
      
    //Generator is a wrapper of StateAction
    Gen(stateAction)
  }
  
  //combinator that uses a choose primitive twice
  def choosePair(start: Int, stopExclusive: Int): Gen[(Int,Int)] = 
    choose(start, stopExclusive) flatMap { 
      x0 => choose(start, stopExclusive) map { x1 => (x0, x1) }
    }
  
  def unit[A](a: => A): Gen[A] = 
    Gen(State(rng => (a, rng)))
    
  def tryUnit[A](a: => A): Gen[Option[A]] =
    unit(a) map { x => Option(x) }
  
  //Generates true or false
  def boolean: Gen[Boolean] = 
    choose(0, 2) map { int => if (int == 0) false else true }
  
  /**
   * Creates a state action that produces lists of size n
   * 
   * @param n - size of the list of elements of type A
   * @param g - generator of elements of type A
   */
  def lisfOfN[A](n: Int, g: Gen[A]): Gen[List[A]] = {
    val stateActions = List.fill(n)(g.next)
    
    Gen(State.sequence(stateActions))
  }
  
  def generateStrings(length: Int): Gen[String] = {
    lisfOfN(length, choose(0, 25)) map { ints => ints map { _.toChar } mkString("") }
  }
  
  //selects either g1 or g2 with equal probability
  def union[A](g1: Gen[A], g2: Gen[A]): Gen[A] = 
    boolean flatMap { b => if (b) g1 else g2 }
  
  def weighted[A](g1: (Gen[A], Double), g2: (Gen[A], Double)): Gen[A] = {
    val p1 = g1._2 / (g1._2 + g2._2) //probability to select g1
    
    Gen(State(RNG.double)) flatMap { p => if (p <= p1) g1._1 else g2._1 }
  }
}