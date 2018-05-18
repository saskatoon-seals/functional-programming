package fpinscala.io

import fpinscala.monads.Monad

//output: IO[Unit]
trait IO[A] {
  import IO.Return, IO.FlatMap
  
  //the interpreter that performs the side-effects
//  def run: A
  
  def flatMap[B](f: A => IO[B]): IO[B] = FlatMap(this, f)
  
  //doesn't need to be defined (comes for "free" via monad trait)
//  def map[B](f: A => B): IO[B] = FlatMap(this, (a: A) => IO.unit(f(a)))
  
  def map[B](f: A => B): IO[B] = flatMap(f andThen (Return(_)))
}

object IO extends Monad[IO] {
  //must!
  def unit[A](a: => A) = Return(a)

  def flatMap[A,B](ma: IO[A])(f: A => IO[B]): IO[B] = ma flatMap f
  
  //so that IO can be constructed as IO { ... } 
  def apply[A,B](a: => A): IO[A] = unit(a)  
  
  def forever[A](ma: IO[A]): IO[A] = {
    lazy val nextMa = forever(ma)
    
    ma flatMap { _ => nextMa }
  }
  
  case class Return[A](a: A) extends IO[A]
  case class Suspend[A](resume: () => A) extends IO[A]
  case class FlatMap[A,B](subTask: IO[A], callback: A => IO[B]) extends IO[B]
}