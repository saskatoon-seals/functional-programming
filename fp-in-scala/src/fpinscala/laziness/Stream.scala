package fpinscala.laziness

import scala.collection.immutable.List;

import Stream._
import scala.annotation.tailrec

sealed trait Stream[+A] {
  def headOption: Option[A] = this match {
    case Empty => None
    //Explicit forcing of h thunk evaluation using h()
    case Cons(h, t) => Some(h())
  }
  
  def toList: List[A] = {
    @tailrec
    def go[A](as: Stream[A], acc: List[A]): List[A] = as match {
      case Cons(h, t) => go(t(), h() :: acc)
      case _ => acc
    }
    
    go(this, List()).reverse
  }
  
  def take(n: Int): Stream[A] =
    this match {
      case Cons(h, t) if n > 1 => cons(h(), t().take(n - 1))
      //prevents unnecessary evaluation of t
      case Cons(h, _) if n == 1 => cons(h(), empty)
      case _ => empty
    }
  
  @tailrec
  final def drop(n: Int): Stream[A] =
    this match {
      case Cons(_, t) if n > 0 => t().drop(n - 1)
      case _ if n == 0 => empty
    }
  
  def takeWhile(p: A => Boolean): Stream[A] = 
    this match {
      case Cons(h, t) if (p(h())) => cons(h(), t() takeWhile p)
      case _ => empty
    }
  
  /*
   * Function f accepts second argument [=> B] as unevaluated (thunk) - 
   * it evaluates it only if needed (lazy evaluation).
   * 
   * If function f terminates without evaluating second argument, 
   * recursion never occurs. 
   * 
   * E.g. (f: x1 || x2; x1 == true) => first argument is enough for the function to produce 
   * the result and terminate.
   */
  def foldRight[B](e: => B)(f: (A, => B) => B): B = 
    this match {
      case Cons(h, t) => f(h(), t().foldRight(e)(f)) 
      case _ => e 
    }
  
  //if p(a) evaluates to false, function terminates
  def forAll(p: A => Boolean): Boolean = 
    foldRight(true)((a, b) => p(a) && b)    
    
  def takeWhile1(p: A => Boolean): Stream[A] = 
    foldRight(empty[A])((a, b) => if (p(a)) cons(a, b) else empty)
    
  def headOption1: Option[A] =
    foldRight(None: Option[A])((a, b) => Some(a))
    
  def map[B](f: A => B): Stream[B] = 
    foldRight(empty[B])((a, b) => cons(f(a), b))
    
  def filter(p: A => Boolean): Stream[A] = 
    foldRight(empty[A])((h, t) => if (p(h)) cons(h, t) else t)
  
  //To avoid contravariance problem
  def append[AA >: A](xs: => Stream[AA]): Stream[AA] = 
    foldRight(xs)((h, t) => cons(h, t))
    
  def flatMap[B](f: A => Stream[B]): Stream[B] = 
    foldRight(empty[B])((x, xs) => f(x).append(xs))
    
  def map1[B](f: A => B): Stream[B] = 
    unfold(this)(stream => stream match {
      case Cons(h, t) => Some((f(h()), t()))
      case _ => None
    })
    
  def take1(n: Int): Stream[A] = 
    unfold((this, n))(x => x match {
      case (Cons(h, t), n) if n > 1 => Some((h(), (t(), n - 1)))
      case (Cons(h, _), n) if n == 1 => Some(h(), (empty, n - 1))
      case _ => None
    })
    
  def takeWhile2(p: A => Boolean): Stream[A] = 
    unfold(this)(stream => stream match {
      case Cons(h, t) if (p(h())) => Some((h(), t()))
      case _ => None
    })
    
  def zipWith[B, C](ys: Stream[B])(f: (A, B) => C): Stream[C] =
    Stream.zip(this, ys)(f)    
   
  def hasSubsequence[A](xs: Stream[A]): Boolean = this match {
    case Empty => false
    case _ if (zipWith(xs)(_ == _)).foldRight(true)(_ && _) => true
    case Cons(h, t) => t().hasSubsequence(xs) 
  }
  
  //Keeps zipping and comparing elements until they are equal or the prefix is exhausted.
  def startsWith[A](prefix: Stream[A]): Boolean = 
    if (this == Empty) false 
    else zipWith(prefix)(_ == _).forAll(identity)

  //For testing purposes only
  def startsWith1[A](prefix: Stream[A]): Boolean = 
    if (this == Empty) false 
    else zipWith(prefix)((a, b) => {println("zip"); a == b}).forAll(a => {println("fold"); a == true})
    
  def tails: Stream[Stream[A]] = {
    def tailsHelper(xs: Stream[A]): Stream[Stream[A]] = xs match {
      case Cons(h, t) => cons(xs, tailsHelper(t()))
      case _ => empty
    }
    
    tailsHelper(this)
  }
  
  def tails1: Stream[Stream[A]] = 
    unfold(this) {
      case Cons(h, t) => Some((cons(h(), t()), t()))
      case _ => None
    }
  
  def tails2: Stream[Stream[A]] = 
    unfold(this) {
      case Empty => None
      case s => Some((s, s.drop(1)))
    }
  
  def scanRight[B](z: => B)(f: (A, => B) => B): Stream[B] = {
    def scanHelper(xs: Stream[A], z: => B)(f: (A, => B) => B): Stream[B] = xs match {
      case Cons(h, t) => cons(xs.foldRight(z)(f), scanHelper(t(), z)(f))
      case _ => Stream(z)
    }
    
    scanHelper(this, z)(f)
  }
  
  @annotation.tailrec
  final def find(f: A => Boolean): Option[A] = this match {
    case Empty      => None
    case Cons(h, t) => if (f(h())) Some(h()) else t().find(f)
  }
  
  // special case of `zipWith`
  def zip[B](s2: Stream[B]): Stream[(A, B)] =
    zipWith(s2)((_, _))
}

case object Empty extends Stream[Nothing]
case class Cons[+A] (h: () => A, t: () => Stream[A]) extends Stream[A]

object Stream {
  //arguments can be passed in "by name" because of this smart constructor
  def cons[A](h: => A, t: => Stream[A]): Stream[A] = {
    //cache the result of evaluations
    lazy val hh = h
    lazy val tt = t
    //thunks must be explicitly forced
    Cons(() => hh, () => tt)
  }
  
  def empty[A]: Stream[A] = Empty
  
  def apply[A](as: A*): Stream[A] = 
    if (as.isEmpty) empty 
    else cons(as.head, apply(as.tail: _*))
    
  def constant[A](a: A): Stream[A] = 
    cons(a, constant(a))
    
  def from(n: Int): Stream[Int] = 
    cons(n, from(n + 1))
    
  def fibs(): Stream[Int] = {
    def go(n1: Int, n0: Int): Stream[Int] = {
      val n2 = n1 + n0;
      cons(n2, go(n2, n1))
    }
      
    cons(0, cons(1, go(1, 0)))
  }
  
  /**
   * Generator (corecursive function)
   * 
   * Produces next value and state based on the current state by a function
   * 
   * A - value type
   * S - state type
   * 
   * z - initial state
   * f- function to produce next (value, state) based on current state
   * 
   * Example of usage:
   * (to generate a stream of random numbers):
   * 
   * Stream.unfold(Random)(random => Some((random.nextInt(), random)))
   */
  def unfold[A, S](z: S)(f: S => Option[(A, S)]): Stream[A] = { 
    val next = f(z);
    
    next match {
      case Some(x) => cons(x._1, unfold(x._2)(f)) 
      case _ => empty[A]
    }
  }
  
  def constant1[A](a: A): Stream[A] = 
    unfold(a)(s => Some((s, s)))
    
  def from1(n: Int): Stream[Int] = 
    unfold(n)(s => Some(s, s + 1))
    
  def fibs1(): Stream[Int] = {
    val tail = unfold((1, 0))(s => Some((s._1 + s._2, (s._1 + s._2, s._1))))
    
    cons(0, cons(1, tail))
  }
  
  def zip[A, B, C](xs: Stream[A], ys: Stream[B])(f: (A, B) => C): Stream[C] =
    unfold((xs, ys)){
      case (Cons(x, xs), Cons(y, ys)) => Some((f(x(), y()), (xs(), ys())))
      case _ => None
    }
    
 def zipAll[A, B](xs: Stream[A], ys: Stream[B]): Stream[(Option[A], Option[B])] =
   unfold((xs, ys))({
      case (Cons(x, xs), Cons(y, ys)) => Some(((Some(x()), Some(y())), (xs(), ys())))
      case (Empty, Cons(y, ys)) => Some(((None, Some(y())), (empty, ys())))
      case (Cons(x, xs), Empty) => Some(((Some(x()), None), (xs(), empty)))
      case _ => None
    })
}