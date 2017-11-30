package fpinscala.purestate

import scala.annotation.tailrec

trait RNG {
  def nextInt: (Int, RNG)
}

//NOTE: case class that implements a trait
case class SimpleRNG(seed: Long) extends RNG {
  //seed is an internal state (member variable) of this class
  
  def nextInt: (Int, RNG) = {
    val newSeed = (seed * 0x5DEECE66DL + 0XBL) & 0xFFFFFFFFFFFFL
    
    ((newSeed >>> 16).toInt, SimpleRNG(newSeed))
  }
}

object RNG {
  def nonNegativeInt(rng: RNG): (Int, RNG) = {
    val (n, newRng) = rng.nextInt
    
    if (n == Int.MinValue) (Int.MaxValue, newRng) else (math.abs(n), newRng) 
  }
  
  //[0, 1)
  def double(rng: RNG): (Double, RNG) = {
    val (n, newRng) = nonNegativeInt(rng)
    
    ((n.toDouble / (Int.MaxValue + 1)), newRng)
  }
  
  def intDouble(rng: RNG): ((Int, Double), RNG) = {
    val (a, rngA) = rng.nextInt
    val (b, rngB) = double(rngA)
    
    ((a, b), rngB)
  }
  
  def doubleInt(rng: RNG): ((Double, Int), RNG) = {
    val (a, rngA) = double(rng)
    val (b, rngB) = rngA.nextInt
    
    ((a, b), rngB)
  }
  
  //temporary assignments of partial results are necessary
  def double3(rng: RNG): ((Double, Double, Double), RNG) = {
    val (a, rngA) = double(rng)
    val (b, rngB) = double(rngA)
    val (c, rngC) = double(rngB)
    
    ((a, b, c), rngC)
  }
  
  def ints(count: Int)(rng: RNG): (List[Int], RNG) = {
    if (count == 0) 
      (List(), rng) 
    else {
      val (n, rngA) = rng.nextInt
      val (list, rngB) = ints(count - 1)(rngA)
      ((n :: list), rngB)
    }
  }
  
  def ints2(count: Int)(rng: RNG): (List[Int], RNG) = {
    @tailrec
    def go(count: Int, rng: RNG, xs: List[Int]): (List[Int], RNG) = {      
      if (count == 0) (List(), rng)
      else {
        val (n, rngA) = rng.nextInt
        go(count - 1, rngA, n :: xs)
      }
    }
    
    go(count, rng, List())
  }
  
  //Alias for a state action (transition)
  type Rand[+A] = RNG => (A, RNG)
  
  //Passes the RNG state through without using it, a is always a constant instead of a random value
  def unit[A](a: A): Rand[A] = 
    rng => (a, rng)
    
  def map[A, B](s: Rand[A])(f: A => B): Rand[B] = 
    rng1 => {
      val (a, rng2) = s(rng1)
      (f(a), rng2)
    }
    
  def doubleWithMap(rng: RNG): (Double, RNG) = {
    map (nonNegativeInt) (n => n.toDouble / (Int.MaxValue + 1)) (rng)
  }
  
  /*
   * map2 is a combinator HOF (combines two rng actions into a single one)
   * using a combinator function f 
   */
  def map2[A, B, C](ra: Rand[A], rb: Rand[B])(f: (A, B) => C): Rand[C] =
    rng => {
      val (a, rngA) = ra(rng)
      val (b, rngB) = rb(rngA)
      
      (f(a, b), rngB) 
    }
    
  /*
   * Combines a list of state transitions into a single state transition
   * 
   * It receives a list of state transitions and it combines them into a single state transition
   * which returns a list of values of type A when it's passed in a random number generator.
   * 
   * Instead of passing multiple random number generators to get a list of values of type A.
   */
  def sequence[A](fs: List[Rand[A]]): Rand[List[A]] = fs match {
    case rand :: t => 
      rng => {
        val (a, rngA) = rand(rng)
        val (as, rngAs) = sequence(t)(rngA)
        
        (a :: as, rngAs)
      }
    case _ =>  unit(List())
  }
  
  /**
   * @param fs - list of state transitions
   * @return a combined state transition 
   */
  def sequence1[A](fs: List[Rand[A]]): Rand[List[A]] =
    fs.foldRight (unit(List[A]())) {
        (rand, result) => 
          rng => {
            val (a, rngA) = rand(rng)
            val (as, rngAs) = result(rngA)
            
            (a :: as, rngAs)
          }
    }
  
  def sequence2[A](fs: List[Rand[A]]): Rand[List[A]] =
    fs.foldRight (unit(List[A]())) ((h, t) => map2(h, t)(_ :: _))
  
  def ints3(count: Int)(rng: RNG): (List[Int], RNG) = {
    //list of state transitions that generate a next integer
    val fs = List.fill[Rand[Int]](count)(_.nextInt)
    
    sequence1(fs)(rng)
  }
  
  //automatically passes the internal state from one state transition to the next one
  def flatMap[A, B](s: Rand[A])(f: A => Rand[B]): Rand[B] = 
    rng => {
      val (a, rngA) = s(rng)
      val (b, rngB) = f(a)(rngA)
      
      (b, rngB)
    }
    
  def nonNegativeLessThan(n: Int): Rand[Int] = {
    flatMap(nonNegativeInt) { i =>
      val mod = i % n
      
      if (i + (n-1) - mod >= 0) rng => (mod, rng)
      else nonNegativeLessThan(n)
    }
  }
  
  def mapAsFlatMap[A, B](s: Rand[A])(f: A => B): Rand[B] =
    flatMap (s) (a => unit(f(a)))
    
 //Two "imperative" sequential steps combined into chaining of flatMaps
 def map2AsFlatMap[A, B, C](ra: Rand[A], rb: Rand[B])(f: (A, B) => C): Rand[C] =
    flatMap (ra) { a => flatMap (rb) (b => unit(f(a, b))) }
}