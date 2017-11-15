package fpinscala.errorhandling

import scala.util.{Left => _, Right => _, Either => _, _}

sealed trait Either[+E, +A] {
  def map[B](f: A => B): Either[E, B] = 
    this match {
      case Left(e) => Left(e)
      case Right(a) => Right(f(a))
    }
  
  //When mapping over right side, left parameter must be promoted to satisfy +E variance
  def flatMap[EE >: E, B](f: A => Either[EE, B]): Either[EE, B] = 
    this match {
      case Left(e) => Left(e)
      case Right(a) => f(a) 
    }
  
  def orElse[EE >: E, B >: A](b: => Either[EE, B]): Either[EE, B] = 
    this match {
      case Left(_) => b
      case Right(a) => Right(a)
    }
  
  def map2[EE >: E, B, C](b: Either[EE, B])(f: (A, B) => C): Either[EE, C] = 
    for {
      aa <- this
      bb <- b
    } yield f(aa, bb)
} 

//Disjoint union of two types (represents values that can be one of two things)
final case class Left[+E](error: E)  extends Either[E, Nothing];
final case class Right[+A](value: A) extends Either[Nothing, A];

object Either {
  def sequence[E, A](es: List[Either[E, A]]): Either[E, List[A]] =
    es match {
      case Nil => Right(Nil)
      case h :: t => h flatMap (hh => sequence(t) map (hh :: _))
    }
  
  def sequence1[E, A](es: List[Either[E, A]]): Either[E, List[A]] =
    es.foldRight [Either[E, List[A]]] (Right(Nil)) ((a, acc) => a flatMap (aa => acc map (aa :: _)))
    
  def sequence2[E, A](es: List[Either[E, A]]): Either[E, List[A]] =
    es.foldRight [Either[E, List[A]]] (Right(Nil)) ((a, acc) => a.map2(acc)(_ :: _))
    
  def sequence3[E, A](es: List[Either[E, A]]): Either[E, List[A]] =
    es match {
      case Nil => Right(Nil)
      case h :: t => h.map2(sequence(t))(_ :: _)
    }
  
  def traverse[E, A, B](as: List[A])(f: A => Either[E, B]): Either[E, List[B]] =
    as.foldRight[Either[E, List[B]]](Right(Nil))((a, acc) => f(a).map2(acc)(_ :: _))
}