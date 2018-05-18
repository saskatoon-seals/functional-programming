package fpinscala.io

import fpinscala.io.IO.{Return => _}
import fpinscala.io.IO.{Suspend => _}
import fpinscala.io.IO.{FlatMap => _}

import fpinscala.monads.Monad

sealed trait Free[F[_],A] {
  import Free.FlatMap, Free.Return
  
  def flatMap[B](f: A => Free[F,B]): Free[F,B] = FlatMap(this, f) 
  
  def map[B](f: A => B): Free[F,B] = FlatMap(this, f andThen (Return(_))) 
}

object Free {
  def freeMonad[F[_]] = new Monad[({type f[a] = Free[F,a]})#f] {
    def unit[A](a: => A): Free[F,A] = Return(a)
    
    def flatMap[A,B](ma: Free[F,A])(f: A => Free[F,B]): Free[F,B] = ma flatMap f 
  }
  
  case class Return[F[_],A](a: A) extends Free[F,A]
  case class Suspend[F[_],A](resume: () => F[A]) extends Free[F,A]
  case class FlatMap[F[_],A,B](subTask: Free[F,A], callback: A => Free[F,B]) extends Free[F,B]
}