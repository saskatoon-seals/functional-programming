package fpinscala.monoids

import fpinscala.datastructures.{List => _}

import fpinscala.propertytesting.Gen
import fpinscala.propertytesting.Prop
import fpinscala.propertytesting.Prop.forAll

import fpinscala.parallelism.AsyncPar._

trait Monoid[A] {
  //needs to satisfy associativity law: op(op(a, b), c) == op(a, op(b, c))
  def op(a1: A, a2: A): A
  
  //op with zero needs to satisfy two identity laws: op(a, zero) == a, op(zero, a) == a
  def zero: A
}

object Monoid {
  val intAddition = new Monoid[Int] {
    def op(a1: Int, a2: Int) = a1 + a2
    val zero = 0
  }
  
  val intMultiplication = new Monoid[Int] {
    def op(a1: Int, a2: Int) = a1 * a2
    val zero = 1
  }
  
  val booleanOr = new Monoid[Boolean] {
    def op(a1: Boolean, a2: Boolean) = a1 | a2
    val zero = false
  }
  
  val booleanAnd = new Monoid[Boolean] {
    def op(a1: Boolean, a2: Boolean) = a1 & a2
    val zero = true
  }
  
  def optionMonoid1[A] = new Monoid[Option[A]] {
    def op(a1: Option[A], a2: Option[A]) = a1 orElse a2
    val zero = None
  }
  
  def reverse[A] (m: Monoid[A]) = new Monoid[A] {
    def op(a1: A, a2: A) = m.op(a2, a1)
    val zero = m.zero
  }
  
  def optionMonoid2[A] = reverse(optionMonoid1)
  
  //monoid for endofunctions
  def endoMonoid1[A]: Monoid[A => A] = new Monoid[A => A] {
    def op(f: A => A, g: A => A) = f compose g
      
    val zero = identity
  }
  
  //f andThen g == g compose f
  def endoMonoid2[A]: Monoid[A => A] = reverse(endoMonoid1)
  
  def monoidLaws[A] (m: Monoid[A], gen: Gen[A]): Prop = {
    val leftIdentity = forAll(gen)(in => m.op(in, m.zero) == gen)
    val rightIdentity = forAll(gen)(in => m.op(m.zero, in) == gen)
    val associativity = forAll(for {
      a1 <- gen
      a2 <- gen
      a3 <- gen
    } yield (a1, a2, a3)) (p => 
      m.op(m.op(p._1, p._2), p._3) == m.op(p._1, m.op(p._2, p._3)) 
    )
    
    leftIdentity && rightIdentity && associativity
  }
  
  def concat[A](as: List[A], m: Monoid[A]): A = as.foldLeft(m.zero)(m.op)

  //non-optimal
  def foldMap[A,B] (as: List[A], m: Monoid[B])(f: A => B): B = concat(as map f, m)
  
  //walks through a list once only
  def foldMap1[A,B] (as: List[A], m: Monoid[B])(f: A => B): B = 
    as.foldLeft(m.zero){ (acc, a) => m.op(acc, f(a)) }
  
  def foldLeft1[A,B] (as: List[A]) (z: B)(f: (B, A) => B): B = {
    foldMap(as, endoMonoid2[B])(a => b => f(b, a))(z)
  }
  
  def foldRight1[A,B] (as: List[A]) (z: B)(f: (A, B) => B): B = {
    foldMap(as, endoMonoid1: Monoid[B => B])(f curried)(z)
  }
  
  //balanced map&fold
  def foldMapV[A,B] (v: IndexedSeq[A], m: Monoid[B])(f: A => B): B = {
    if (v.length == 0) 
      m.zero  
    else if (v.length == 1)
      f(v.head)
    else {
      val (left, right) = v.splitAt((v.length * 0.5).toInt)
      
      m.op(
          foldMapV(left, m)(f), 
          foldMapV(right, m)(f)
      ) 
    }
  }
  
  def par[A](m: Monoid[A]): Monoid[AsyncPar[A]] = {
    new Monoid[AsyncPar[A]] { 
      def op(par1: AsyncPar[A], par2: AsyncPar[A]) = map2(par1, par2)(m.op)
      def zero = unit(m.zero)
    }
  }

  //What is a difference between these two methods?
  //Is only map executing in parallel but reduce isn't?
  def parFoldMap[A,B] (as: IndexedSeq[A], m: Monoid[B])(f: A => B): AsyncPar[B] = {
    foldMapV(as, par(m))(a => fork(unit(f(a))))
  }
  
  //mapping and reducing BOTH execute in parallel
  def parFoldMap1[A,B] (as: IndexedSeq[A], m: Monoid[B])(f: A => B): AsyncPar[B] = {
    flatMap(parMap(as)(f)) { 
      bs => foldMapV(bs, par(m))(b => fork(unit(b))) 
    }
  }
  
  def isOrdered[A](as: IndexedSeq[A], compare: (A, A) => Boolean): Boolean = {
    val m = new Monoid[(List[A], Boolean)]{
      def zero = (List(), true)
      def op(x1: (List[A], Boolean), x2: (List[A], Boolean)) = (x1, x2) match {
        case ((l1, b1), (l2, b2)) => (l1 ++ l2, b1 && b2 && compare(l1.head, l2.last)) 
      }
    }
    
    foldMapV(as, m)(a => (List(a), true))._2
  }
  
  sealed trait WC //word count
  case class Stub(chars: String) extends WC //haven't seen any complete words yet
  
  case class Part(lStub: String, words: Int, rStub: String) extends WC {
    override def equals(that: Any): Boolean = that match {
      case that: Part => lStub == that.lStub && words == that.words && rStub == that.rStub
      case _ => false
    }
  }

  val wcMonoid = new Monoid[WC] {
    def zero = Stub("")
    def op(left: WC, right: WC) = (left, right) match {
      case (Stub(ls), Stub(rs)) => Stub(ls + rs)
      case (Stub(ls1), Part(ls2, w2, rs2)) => Part(ls1 + ls2, w2, rs2) 
      case (Part(ls1, w1, rs1), Stub(rs2)) => Part(ls1, w1, rs1 + rs2) 
      case (Part(ls1, w1, rs1), Part(ls2, w2, rs2)) => 
        Part(ls1, w1 + w2 + (if ((rs1 + ls2).isEmpty()) 0 else 1), rs2)
    }
  }
  
  def countWords(input: String): Int = {
    def unstub(s: String) = if (s.isEmpty()) 0 else 1
    
    foldMapV(input.toIndexedSeq, wcMonoid) {
        c => if (c == ' ') Part("", 0, "") else Stub(c.toString())
    } match {
      case Stub(s) => unstub(s)
      case Part(l, count, r) => count + unstub(l) + unstub(r) 
    }
  }
  
  //composing monoids: product of two monoids is also a monoid
  def productMonoid[A,B](A: Monoid[A], B: Monoid[B]) = new Monoid[(A, B)] {
    def zero = (A.zero, B.zero)
    def op(ab1: (A, B), ab2: (A, B)) = (ab1, ab2) match {
      case ((a1, b1), (a2, b2)) => (A.op(a1, a2), B.op(b1, b2))
    }
  }
  
  def functionMonoid[A,B](B: Monoid[B]) = new Monoid[A => B] {
    def zero = _ => B.zero
    def op(f: A => B, g: A => B) = (a: A) => B.op(f(a), g(a)) 
  }
  
  def mapMergeMonoid[K,V](V: Monoid[V]) = new Monoid[Map[K,V]] {
    def zero = Map[K,V]()
    def op(a: Map[K, V], b: Map[K, V]) = 
      (a.keySet ++ b.keySet).foldLeft(zero) { (acc, k) =>
        //updated returns a new immutable map
        acc.updated(k, V.op(a.getOrElse(k, V.zero), b.getOrElse(k, V.zero)))
      }
  }
  
  //Map[A, Int] is a bag data structure
  def bag[A](as: IndexedSeq[A]): Map[A, Int] = {
    foldMapV(as, mapMergeMonoid(intAddition): Monoid[Map[A, Int]])(a => Map(a -> 1))
  }
}