package fpinscala.monoids

import fpinscala.datastructures.Tree
import fpinscala.datastructures.Leaf
import fpinscala.datastructures.Branch

trait Foldable[F[_]] {
  def foldRight[A,B](as: F[A])(z: B)(f: (A,B) => B): B = 
    foldMap(as)(f.curried)(Monoid.endoMonoid1)(z)
  
  def foldLeft[A,B](as: F[A])(z: B)(f: (B,A) => B): B =
    foldMap(as)(a => { (b: B) => f(b, a) })(Monoid.endoMonoid2)(z)
  
  def foldMap[A,B](as: F[A])(f: A => B)(m: Monoid[B]): B = 
    foldRight(as)(m.zero)((a, b) => m.op(f(a), b))
  
  def concatenate[A](as: F[A])(m: Monoid[A]): A = 
    foldLeft(as)(m.zero)(m.op)
    
  def toList[A](fa: F[A]): List[A] = {
    foldRight(fa)(List(): List[A])(_ :: _)   
  }
}

object FList extends Foldable[List] {
  override def foldRight[A,B](as: List[A])(z: B)(f: (A,B) => B): B = as.foldRight(z)(f)
  
  override def foldLeft[A,B](as: List[A])(z: B)(f: (B,A) => B): B = as.foldLeft(z)(f)
  
  override def foldMap[A,B](as: List[A])(f: A => B)(m: Monoid[B]): B = 
    as.foldLeft(m.zero)((b, a) => m.op(b, f(a)))
}

object FIndexedSeq extends Foldable[IndexedSeq] {
  override def foldRight[A,B](as: IndexedSeq[A])(z: B)(f: (A,B) => B): B = as.foldRight(z)(f)
  
  override def foldLeft[A,B](as: IndexedSeq[A])(z: B)(f: (B,A) => B): B = as.foldLeft(z)(f)
  
  override def foldMap[A,B](as: IndexedSeq[A])(f: A => B)(m: Monoid[B]): B = 
    Monoid.foldMapV(as, m)(f)
}

object FStream extends Foldable[Stream] {
  override def foldRight[A,B](as: Stream[A])(z: B)(f: (A,B) => B): B = as.foldRight(z)(f)
  
  override def foldLeft[A,B](as: Stream[A])(z: B)(f: (B,A) => B): B = as.foldLeft(z)(f)
  
  override def foldMap[A,B](as: Stream[A])(f: A => B)(m: Monoid[B]): B = 
    as.foldLeft(m.zero)((b, a) => m.op(b, f(a)))
}

object FTree extends Foldable[Tree] {
  override def foldRight[A, B](tree: Tree[A])(z: B)(f: (A, B) => B): B = 
    Tree.fold(tree)(f.curried)((l, r) => l compose r)(z)
  
  override def foldLeft[A,B](tree: Tree[A])(z: B)(f: (B,A) => B): B = 
    Tree.fold(tree)(a => { (b: B) => f(b, a) })((l, r) => l compose r)(z)
  
  override def foldMap[A,B](tree: Tree[A])(f: A => B)(m: Monoid[B]): B = 
    Tree.fold(tree)(f)(m.op)
}

object FOption extends Foldable[Option] {
  override def foldRight[A, B](option: Option[A])(z: B)(f: (A, B) => B): B = 
    option map (a => f(a, z)) getOrElse z
    
  override def foldLeft[A,B](option: Option[A])(z: B)(f: (B,A) => B): B = 
    option map (a => f(z, a)) getOrElse z
  
  override def foldMap[A,B](option: Option[A])(f: A => B)(m: Monoid[B]): B = 
    option map f getOrElse m.zero
}
