package fpinscala.datastructures

sealed trait Tree[+A]

case class Leaf[A] (value: A) extends Tree[A]
case class Branch[A] (left: Tree[A], right: Tree[A]) extends Tree[A]

object Tree {
  def size[A](t: Tree[A]): Int = t match {
    case Leaf(_) => 1
    case Branch(l, r) => size(l) + size(r) + 1
  }
  
  def size1[A](t: Tree[A]): Int =
    fold(t)(_ => 1)((l, r) => l + r + 1)
  
  def maximum(t: Tree[Int]): Int = t match {
    case Leaf(v) => v
    case Branch(l, r) => maximum(l) max maximum(r)
  }
  
  def maximum1(t: Tree[Int]): Int =
    fold(t)(identity)(_ max _)
  
  def depth[A](t: Tree[A]): Int = t match {
    case Leaf(_) => 1
    case Branch(left, right) => 1 + (depth(left) max depth(right))
  }
  
  def depth1[A](t: Tree[A]): Int = 
    fold(t)(_ => 1)((l, r) => 1 + (l max r))
  
  def map[A, B](t: Tree[A])(f: A => B): Tree[B] = t match {
    case Leaf(v) => Leaf(f(v))
    case Branch(l, r) => Branch(map(l)(f), map(r)(f))
  }
  
  def fold[A, B](t: Tree[A])(e: A => B)(f: (B, B) => B): B = t match {
    case Leaf(v) => e(v)
    case Branch(l, r) => f(fold(l)(e)(f), fold(r)(e)(f))
  }
  
  //If result is not provided, Leaf[B] inferred instead of Tree[B] as arguments for combiner fun.
  def map1[A, B](t: Tree[A])(f: A => B): Tree[B] =
    fold (t) (v => Leaf(f(v)): Tree[B]) (Branch(_, _))
}