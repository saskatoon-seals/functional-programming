package fpinscala.functors.applicative

import fpinscala.laziness.Stream
import fpinscala.functors.normal.Functor
import scala.Vector

trait Applicative2[F[_]] extends Functor[F] {
  //primitives - only unit?:
  
  def unit[A](a: => A): F[A]
  
  //fab is a functor containing a function
  def apply[A,B](fab: F[A => B])(fa: F[A]): F[B] = map2(fab, fa)(_(_))
  
  //derived combinators:
  
  def map[A,B](fa: F[A])(f: A => B): F[B] = apply(unit(f))(fa)
    
  //implemented with function composition
  def map2[A,B,C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] = {
    val fac: F[A => C] = map(fb){ b => (a => (f(a, b))) }
    
    apply(fac)(fa)
  }
  
  def sequence[A](as: List[F[A]]): F[List[A]] = 
    as.foldRight(unit(List(): List[A])){ (h, t) => map2(h, t)(_ :: _)}
  
  //implemented with currying
  def map2Alt[A,B,C](fa: F[A], fb: F[B])(f: (A, B) => C): F[C] = 
    apply(apply(unit(f.curried))(fa))(fb)
  
  def map3[A,B,C,D](fa: F[A], fb: F[B], fc: F[C])(f: (A, B, C) => D): F[D] = {
    val fabcd: F[A => B => C => D] = unit(f.curried)
    
    apply(apply(apply(fabcd)(fa))(fb))(fc)
  }
  
  def map4[A,B,C,D,E](fa: F[A], fb: F[B], fc: F[C], fd: F[D])(f: (A, B, C, D) => E): F[E] = 
    apply(apply(apply(apply(unit(f.curried))(fa))(fb))(fc))(fd)  
    
  def product[G[_]](G: Applicative2[G]): Applicative2[({type f[x] = (F[x], G[x])})#f] = {
    //this inside new refers to the new instance being created
    val self: Applicative2[F] = this
    
    new Applicative2[({type f[x] = (F[x], G[x])})#f] {
      def unit[A](a: => A) = (self.unit(a), G.unit(a))
  
      override def apply[A,B](fab: (F[A => B], G[A => B]))(fa: (F[A], G[A])) = 
        (
            self.apply(fab._1)(fa._1), 
            G.apply(fab._2)(fa._2)
        )
    }
  }
  
  def compose[G[_]](G: Applicative2[G]) = { 
    val self: Applicative2[F] = this
    
    new Applicative2[({type f[x] = F[G[x]]})#f] {
      def unit[A](a: => A) = self.unit(G.unit(a))
  
      override def map2[A,B,C](fga: F[G[A]], fgb: F[G[B]])(f: (A,B) => C): F[G[C]] = 
        self.map2(fga, fgb) {
          (ga, gb) => G.map2(ga, gb)(f)
        } 
    }
  } 
  
  def sequenceMap[K,V](ofa: Map[K, F[V]]): F[Map[K, V]] = {
    ofa.foldRight(unit(Map(): Map[K, V])){ (h, t) => 
      map2(h._2, t){ (v: V, tt) => Map(h._1 -> v) ++ tt }
    }
  }
}

object Applicative2 {
  val stream = new Applicative2[Stream] {
    def unit[A](a: => A): Stream[A] = Stream.constant(a)
    
    override def map2[A,B,C](fa: Stream[A], fb: Stream[B])(f: (A, B) => C): Stream[C] =
      Stream.zip(fa, fb)(f)
  }
  
  def validation[E] = new Applicative2[({type m[a] = Validation[E, a]}) # m] {
    def unit[A](a: => A): Validation[E, A] = Success(a)
    
    override def map2[A,B,C](fa: Validation[E, A], fb: Validation[E, B])(f: (A, B) => C): Validation[E, C] =
      (fa, fb) match {
        case (Success(a), Success(b)) => Success(f(a, b))
        case (Failure(h1, t1), Failure(h2, t2)) => Failure(h1, t1 ++ Vector(h2) ++ t2)
        case (_, e@Failure(_, _)) => e
        case (e@Failure(_, _), _) => e
      }
  }
}