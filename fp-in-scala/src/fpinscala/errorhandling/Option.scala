package fpinscala.errorhandling

import scala.collection.immutable.Nil
import scala.{Option => _, Some => _}

sealed trait Option[+A] {
  def map[B](f: A => B): Option[B] = this match {
    case None => None
    case Some(a) => Some(f(a))
  }
 
  //B is a supertype of A
  def getOrElse[B >: A](default: => B): B = this match {
    case None => default
    case Some(a) => a
  }
  
  //apply f which may fail
  def flatMap[B](f: A => Option[B]): Option[B] = 
    map(f).getOrElse(None)
  
  //Lazy evaluation of ob
  def orElse[B >: A](ob: => Option[B]): Option[B] = 
    map(Some(_)).getOrElse(ob)
  
  def filter(f: A => Boolean): Option[A] = 
    flatMap(a => if (f(a)) Some(a) else None)
}
case class Some[+A](get: A) extends Option[A]
case object None            extends Option[Nothing]

object Option {
  def map2[A, B, C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] = 
    a flatMap (aa => b map (bb => f(aa, bb)))
  
  /*
   * For-comprehension:
   *  - bindings become flatMaps and yield becomes a map by compiler's desugaring
   *  - use instead of explicit flatMap calls ending with a map
   *  
   * Functional programming looks like imperative (sequential) programming
   */
  def map21[A, B, C](a: Option[A], b: Option[B])(f: (A, B) => C): Option[C] = {
    //for-comprehension == sequence of bindings
    for {
      //1st binding sequence
      aa <- a //this is converted to a flatMap
      //2nd binding sequence
      bb <- b
    } yield f(aa, bb) //this is converted to a map
  }
  
  def sequence[A](as: List[Option[A]]): Option[List[A]] = as match {
    case Nil => Some(Nil)
    case h :: t => h flatMap (hh => sequence(t) map (hh :: _))
  }
  
  /*
   * Parameters of the binary combinator function (a, acc) are:
   *  - a: Option[A]
   *  - acc: Option[List[A]]
   * 
   * Function 
   *  add[A](as: List[A], a: A): List[A] 
   * was lifted into:
   *  add[A](as: Option[List[A]], a: Option[A]): Option[List[A]]
   * 
   * therefore: 
   *  - List[A] -> Option[List[A]]
   *  - A -> Option[A]
   */
  def sequence1[A](as: List[Option[A]]): Option[List[A]] = {
    as.foldRight [Option[List[A]]] (Some(Nil))((a, acc) => map2(a, acc)(_ :: _))
  }

  //I think this is the cleanest solution because it doesn't require a flatMap
  def sequence2[A](as: List[Option[A]]): Option[List[A]] = as match {
    case Nil => Some(Nil)
    case h :: t => map2 (h, sequence(t)) (_ :: _)
  }
  
  def sequence3[A](as: List[Option[A]]): Option[List[A]] = {
    traverse(as)(identity)
  }
    
  def traverse[A, B](as: List[A])(f: A => Option[B]): Option[List[B]] = as match {
    case Nil => Some(Nil)
    case h :: t => map2 (f(h), traverse(t)(f)) (_ :: _)
  }
  
  def traverse1[A, B](as: List[A])(f: A => Option[B]): Option[List[B]] = {
    as.foldRight [Option[List[B]]] (Some(Nil))((a, acc) => map2(f(a), acc)(_ :: _))
  }
}