package fpinscala.functors.traversable

import fpinscala.monoids.Foldable
import fpinscala.monads.Monad
import fpinscala.functors.applicative.Applicative2
import fpinscala.functors.normal.Functor

import fpinscala.purestate.State

trait Traverse[T[_]] extends Functor[T] with Foldable[T] {
  //married functions:
  def traverse[G[_],A,B](ta: T[A])(f: A => G[B])(implicit G: Applicative2[G]): G[T[B]] 
//    sequence(map(fa)(f))
  
  def sequence[G[_],A](tga: T[G[A]])(implicit G: Applicative2[G]): G[T[A]] =
    traverse(tga)(identity)
    
  def map[A,B](ta: T[A])(f: A => B): T[B] = {
    type Id[A] = A
    
    val idMonad = new Monad[Id] {
      def unit[A](a: => A) = a
      override def flatMap[A,B](a: A)(f: A => B): B = f(a) //it implies that A and B are both monads
    }
    
    traverse[Id, A, B](ta)(f)(idMonad)
  }
  
  def traverseS[S,A,B](ta: T[A])(f: A => State[S, B]): State[S, T[B]] = 
    traverse[({type g[x] = State[S, x]})#g, A, B](ta)(f)(Monad.state3)
    
  def mapAccum[S,A,B](ta: T[A], s0: S)(f: (A, S) => (B, S)): (T[B], S) = 
    traverseS(ta)(a => State((s: S) => f(a, s))).run(s0)
  
  //concrete (specific, non-general) methods:
    
  def zipWithIndex[A](ta: T[A]): T[(A, Int)] = 
    mapAccum(ta, 0)((a, s) => ((a, s), s + 1))._1 
    
  override def toList[A](ta: T[A]): List[A] =
    mapAccum(ta, List[A]())((a, s) => ((), a :: s))._2.reverse
    
  def reverse[A](ta: T[A]): T[A] = {
    val as: List[A] = toList(ta).reverse
    
    map(zipWithIndex(ta))(ai => as(ai._2))
  }
  
  def reverse1[A](ta: T[A]): T[A] = 
    mapAccum(ta, toList(ta).reverse)((_, as) => (as.head, as.tail))._1
    
  override def foldLeft[A,B](ta: T[A])(acc: B)(f: (B, A) => B): B = 
    mapAccum(ta, acc)((a, acc) => ((), f(acc, a)))._2
    
  //traverse a structure only once with the two applicatives producing a product
  def fuse[G[_],H[_],A,B](ta: T[A])(f: A => G[B], g: A => H[B])
    (G: Applicative2[G], H: Applicative2[H]): (G[T[B]], H[T[B]]) = 
      traverse[({type f[x] = (G[x], H[x])})#f,A,B](ta)(a => (f(a), g(a)))(G.product(H))
      
  //nested traversal
  def compose[G[_]](implicit G: Traverse[G]): Traverse[({type f[x] = T[G[x]]})#f] = {
    val self = this
    
    new Traverse[({type f[x] = T[G[x]]})#f] {
      override def traverse[M[_]:Applicative2,A,B](tga: T[G[A]])(f: A => M[B]) =
        self.traverse(tga)((ga: G[A]) => G.traverse(ga)(f))
    }
    
  }
}

object Traverse {
  val list = new Traverse[List] {
    override def traverse[G[_], A, B](fa: List[A])(f: A => G[B])(implicit G: Applicative2[G]): G[List[B]] =
      fa.foldRight(G.unit(List[B]())){ (h, t) => G.map2(f(h), t)(_ :: _) }
  }
  
  val option = new Traverse[Option] {
    override def traverse[G[_],A,B](fa: Option[A])(f: A => G[B])(implicit G: Applicative2[G]): G[Option[B]] = 
      fa match {
        case None => G.unit(None)
        case Some(a) => G.map(f(a))(b => Some(b))
      }
  }
  
  case class Tree[+A](head: A, tail: List[Tree[A]])
  
  val tree = new Traverse[Tree] {
    override def traverse[G[_],A,B](fa: Tree[A])(f: A => G[B])(implicit G: Applicative2[G]): G[Tree[B]] = {
      fa match {
        case Tree(head, tail) => G.map2(
            f(head),
            list.traverse(tail)(tree => traverse(tree)(f))
          ){ (b, tb) => tb match {
            case (h :: t) => Tree(b, h :: t)
            case Nil => Tree(b, Nil)
          }}
      }
    }
  }
}