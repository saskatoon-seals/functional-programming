package fpinscala.functors.applicative

import fpinscala.functors.normal.Functor

trait Applicative[F[_]] extends Functor[F] {
  //primitive combinators
  def map2[A,B,C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C]
  
  def unit[A](a: => A): F[A]
  
  //derived combinators
  def map[A,B](fa: F[A])(f: A => B): F[B] = 
    map2(fa, unit(())) { (a, _) => f(a) }
  
  def traverse[A,B](as: List[A])(f: A => F[B]): F[List[B]] = 
    as.foldRight(unit(List(): List[B])) { (h, t) =>
      map2(f(h), t)(_ :: _)
    }
  
  //transplanted from Monad
  def sequence[A](as: List[F[A]]): F[List[A]] = 
    as.foldRight(unit(List(): List[A])){ (h, t) => map2(h, t)(_ :: _)}
  
  def replicateM[A](n: Int, fa: F[A]): F[List[A]] = 
    sequence(List.fill(n)(fa))
    
  def product[A,B](fa: F[A], fb: F[B]): F[(A,B)] = 
    map2(fa, fb)((a, b) => (a, b))
}