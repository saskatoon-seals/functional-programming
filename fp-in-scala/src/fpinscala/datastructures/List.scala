package fpinscala.datastructures

import scala.annotation.tailrec

/*
 * trait - interface
 * sealed - local to this file
 * +A - covariant list => List[Dog] is a subtype of List[Animal] if Dog is a subtype of Animal
 */
sealed trait List1[+A]

//Nothing - subtype of all types => (List[Double] = Nil) can be created
case object Nil extends List1[Nothing]
case class Cons[+A] (head: A, tail: List1[A]) extends List1[A]

//companion object to the List trait
object List1 {
  def foldRight[A,B] (as: List1[A], e: B) (f: (A, B) => B): B = as match {
    case Nil => e
    case Cons(x, xs) => f(x, foldRight(xs, e)(f))
  }
  
  def foldRight1[A,B] (as: List1[A], e: B) (f: (A, B) => B): B = 
    foldLeft(reverse(as), e)((a, b) => f(b, a))
  
  @tailrec
  def foldLeft[A,B] (as: List1[A], acc: B) (f: (B, A) => B): B = as match {
    case Nil => acc
    case Cons(x, xs) => foldLeft(xs, f(acc, x))(f)
  }
  
  def sum(xs: List1[Int]): Int = xs match {
    case Nil => 0
    case Cons(x, xs) => x + sum(xs)
  }
  
  def sum1(xs: List1[Int]): Int = 
    foldLeft(xs, 0)(_ + _)    
  
  def product(xs: List1[Double]): Double = xs match {
    case Nil => 1.0
    //short-circuit:
    case Cons(0.0, _) => 0.0
    case Cons(x, xs) => x * product(xs)
  }
  
  def product1(xs: List1[Double]): Double = 
    foldLeft(xs, 1.0)(_ * _)
  
  /*
   * So that List(1, 2, 3, 4) can be constructed - conveniance method
   * A* - variable number of arguments
   * _* allows us to pass Seq to a variadic method
   */
  def apply[A] (xs: A*): List1[A] = 
    if (xs.isEmpty) Nil
    else Cons(xs.head, apply(xs.tail: _*))

  //Match runtime exception in the case of Nil, could return Nil or a more specific exception
  def tail[A] (xs: List1[A]): List1[A] = xs match {
    case Cons(x, xs) => xs
  }
  
  def head[A] (xs: List1[A]): A = xs match {
      case Cons(x, xs) => x
  }
  
  def setHead[A] (xs: List1[A], head: A): List1[A] = xs match {
    case Cons(x, xs) => Cons(head, xs)
  }
  
  def drop[A] (xs: List1[A], n: Int): List1[A] = n match {
    case 0 => xs
    case _ => drop(tail(xs), n-1) 
  }
  
  /*
   * Type inferrence via currying:
   * called as: dropWhile1(List(1, 2))(x => x < 2)
   */
  def dropWhile[A] (xs: List1[A]) (p: A => Boolean): List1[A] = xs match {
    case Cons(h, t) if (p(h)) => dropWhile(t)(p)
    case _ => xs
  }
  
  def init[A] (xs: List1[A]): List1[A] = xs match {
    case Nil => throw new NotImplementedError
    case Cons(_, Nil) => Nil
    case Cons(x, xs) => Cons(x, init(xs)) 
  }
  
  //TODO: Can init1 be built with map-reduce?
  
  def length[A](xs: List1[A]): Int = 
    foldRight(xs, 0)((x, acc) => acc + 1)
    
  def length1[A](xs: List1[A]): Int = 
    foldLeft(xs, 0)((acc, x) => acc + 1)
    
  def append[A] (xs: List1[A], ys: List1[A]): List1[A] = xs match {
    case Nil => ys
    case Cons(h, t) => Cons(h, append(t, ys)) 
  }
  
  def append1[A] (xs: List1[A], ys: List1[A]): List1[A] = 
    foldRight (xs, ys) (Cons(_, _))
    
  def append2[A] (xs: List1[A], ys: List1[A]): List1[A] = 
    foldLeft (reverse(xs), ys) ((acc, x) => Cons(x, acc))
    
  def reverse[A](as: List1[A]): List1[A] = as match {
    case Nil => Nil
    case Cons(x, xs) => append(reverse(xs), Cons(x, Nil)) 
  }
  
  def reverse1[A](as: List1[A]): List1[A] =
    foldRight (as, Nil:List1[A]) ((x, xs) => append(xs, Cons(x, Nil)))
    
  def reverse2[A](as: List1[A]): List1[A] =
    foldLeft (as, Nil:List1[A]) ((acc, x) => Cons(x, acc))
    
  def flatten[A](xs: List1[List1[A]]): List1[A] = 
    foldRight(xs, Nil:List1[A])(append)
     
  def addNum(xs: List1[Int], n: Int): List1[Int] = 
    foldRight(xs, Nil:List1[Int]) ((h, t) => Cons(h + n, t))
    
  def toString[A](xs: List1[A]): List1[String] = 
    foldRight(xs, Nil:List1[String])((h, t) => Cons(h.toString(), t))
    
  def map[A,B](xs: List1[A])(f: A => B): List1[B] = 
    foldRight(xs, Nil:List1[B]) ((h, t) => Cons(f(h), t))
    
  def filter[A](xs: List1[A])(p: A => Boolean): List1[A] =  
    foldRight(xs, Nil:List1[A])((h, t) => if (p(h)) Cons(h, t) else t)
  
  //flatMap is just simply superior to foldRight in the case of filter
  def filter1[A](xs: List1[A])(p: A => Boolean): List1[A] = 
    flatMap(xs)(x => if (p(x)) List1(x) else Nil)
  
  def flatMap[A,B](xs: List1[A])(f: A => List1[B]): List1[B] = 
    flatten(map(xs)(f))
    
  def zip[A, B, C](xs: List1[A], ys: List1[B])(f: (A, B) => C): List1[C] = (xs, ys) match {
    case (Nil, _) => Nil
    case (_, Nil) => Nil
    case (Cons(x, xs), Cons(y, ys)) => Cons(f(x, y), zip(xs, ys)(f))
  }
    
  @tailrec
  def hasSubsequence[A](sup: List1[A], sub: List1[A]): Boolean = sup match {
    case _ if foldRight (zip(sup, sub)(_ == _), true) (_ && _) => true 
    case Cons(h, t) if length(t) >= length(sub) => hasSubsequence(t, sub)
    case _ => false
  }
  
  @tailrec
  def startsWith[A](l: List1[A], prefix: List1[A]): Boolean = (l, prefix) match {
    case (_, Nil) => true
    case (Cons(x, xs), Cons(y, ys)) if x == y => startsWith(xs, ys)
    case _ => false
  }
  
  def startsWith1[A](xs: List1[A], prefix: List1[A]): Boolean = 
    if (xs == Nil) false 
    else foldRight(zip(xs, prefix)((a, b) => {println("zip"); a == b}), true)((a, b) => {println("fold"); a && b})
  
  @tailrec
  def hasSubsequence1[A](sup: List1[A], sub: List1[A]): Boolean = sup match {
    case Nil => false
    case _ if startsWith(sup, sub) => true
    case Cons(h, t) => hasSubsequence1(t, sub)
  }
}
