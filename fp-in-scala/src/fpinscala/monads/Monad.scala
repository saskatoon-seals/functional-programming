package fpinscala.monads

import fpinscala.functors.traversable.Traverse
import fpinscala.functors.applicative.Applicative2

import fpinscala.propertytesting.Gen

import fpinscala.parallelism.AsyncPar.AsyncPar
import fpinscala.parallelism.AsyncPar

import fpinscala.parsing.Parsers

import fpinscala.errorhandling.Some
import fpinscala.errorhandling.None
import fpinscala.errorhandling.Option

import fpinscala.errorhandling.Either
import fpinscala.errorhandling.Left
import fpinscala.errorhandling.Right

import fpinscala.laziness.Stream

import fpinscala.datastructures.List
import fpinscala.datastructures.Cons

import fpinscala.purestate.State

//M[_] is a type constructor (a monad itself?)
trait Monad[M[_]] extends Applicative2[M] {
  //abstract/primitive operations (minimal set):
  override def unit[A](a: => A): M[A]
  
  def flatMap[A,B](ma: M[A])(f: A => M[B]): M[B]
  
  //derived operations (default implementations):
  override def map[A,B](ma: M[A])(f: A => B): M[B] =
    flatMap(ma)(a => unit(f(a)))
  
  override def map2[A,B,C](ma: M[A], mb: M[B])(f: (A, B) => C): M[C] = 
    flatMap(ma)(a => map(mb)(b => f(a, b)))
    
  def sequence[A](lma: List[M[A]]): M[List[A]] = 
    List.foldRight(lma, unit(List(): List[A])) {(ma, mla) => 
      map2(ma, mla)(Cons(_, _)) 
    } 
  
  def traverse[A,B](la: List[A])(f: A => M[B]): M[List[B]] = sequence(List.map(la)(f))
  
  //traverses the list once only!
  def traverse1[A,B](la: List[A])(f: A => M[B]): M[List[B]] = {
    List.foldRight(la, unit(List(): List[B])) {(a, mla) => 
      map2(f(a), mla)(Cons(_, _)) 
    }
  }
  
  def replicateM[A](n: Int, ma: M[A]): M[List[A]] = sequence(List.listOfN(ma, n))
  
  //E.g.: get a powerset
  def filterM[A](as: List[A])(f: A => M[Boolean]): M[List[A]] = {
    List.foldRight(as, unit(List(): List[A])) { (a, mas) =>
      val mbool = f(a)
      
      flatMap(mbool){ bool =>
        if (bool)
          map2(map(mbool)(_ => a), mas)(Cons(_, _))
        else 
          mas
      }
    }
  }
  
  //Kleisli composition
  def compose[A,B,C](f: A => M[B])(g: B => M[C]): A => M[C] = 
    a => flatMap(f(a))(g)
    
  def flatMap1[A,B](ma: M[A])(f: A => M[B]): M[B] = 
    compose((_: Unit) => ma)(f)()    
    
  //In the case of a list it means: flatten and concatenate
  def join[A](mma: M[M[A]]): M[A] = flatMap(mma)(identity)
  
  def flatMap2[A,B](ma: M[A])(f: A => M[B]): M[B] = 
    join(map(ma)(f))
    
  def compose1[A,B,C](f: A => M[B])(g: B => M[C]): A => M[C] =
    a => join(map(f(a))(g))
    
  //M and G would need to be swapped
//  def composeG[G[_]](G: Monad[G]) = {
//    val self = this
//      
//    new Monad[({type f[x] = M[G[x]]})#f] {
//      override def unit[A](a: => A) = self.unit(G.unit(a))
//  
//      def flatMap[A,B](mga: M[G[A]])(f: A => M[G[B]]): M[G[B]] = {
//        self.flatMap(mga){ ga => G.flatMap(ga)(a => f(a)) }
//      }
//    }
//  }
}

object Monad {
  //we get map and map2 as an addition for free!
  val generator = new Monad[Gen] {
    def unit[A](a: => A) = Gen.unit(a)
    def flatMap[A,B](gen: Gen[A])(f: A => Gen[B]) = gen flatMap f
  }
  
  val asyncPar = new Monad[AsyncPar] {
    def unit[A](a: => A) = AsyncPar.unit(a)
    def flatMap[A,B](par: AsyncPar[A])(f: A => AsyncPar[B]) = AsyncPar.flatMap(par)(f)
  }
  
  trait Parser[+A];
  val parsers: Parsers[Parser] = null
  
  val parser = new Monad[Parser] {
    def unit[A](a: => A) = parsers.unit(a)
    def flatMap[A,B](parser: Parser[A])(f: A => Parser[B]) = parsers.flatMap(parser)(f)
  }
  
  val option = new Monad[Option] {
    def unit[A](a: => A) = Some(a)
    def flatMap[A,B](option: Option[A])(f: A => Option[B]) = option flatMap f
  }
  
  val stream = new Monad[Stream] {
    def unit[A](a: => A) = Stream(a)
    def flatMap[A,B](as: Stream[A])(f: A => Stream[B]) = as flatMap f
  }
  
  val list = new Monad[List] {
    def unit[A](a: => A) = List(a)
    def flatMap[A,B](as: List[A])(f: A => List[B]) = List.flatMap(as)(f)
  }
  
  def state1[S] = {
    type State1[A] = State[S, A]
    
    new Monad[State1] {
      def unit[A](a: => A) = State.unit(a)
      def flatMap[A,B](state: State1[A])(f: A => State1[B]) = state flatMap f
    }
  }
  
  def state2[S] = {
    type State1[A] = State[S, A]
    
    new Monad[State1] {
      def unit[A](a: => A) = State.unit(a)
      def flatMap[A,B](state: State[S, A])(f: A => State[S, B]) = state flatMap f
    }
  }
  
  //g is a "type lambda" == anonymous type constructor that has a partially applied S
  def state3[S] = new Monad[({type g[x] = State[S, x]}) # g] {
    def unit[A](a: => A) = State.unit(a)
    def flatMap[A,B](state: State[S, A])(f: A => State[S, B]) = state flatMap f
  }
  
  def eitherMonad[E] = new Monad[({type m[a] = Either[E, a]}) # m] {
    def unit[A](a: => A): Either[E, A] = Right(a)
  
    def flatMap[A,B](ma: Either[E, A])(f: A => Either[E, B]): Either[E, B] =  ma flatMap f
  }
  
  //G is both a monad and traversable while M is only a monad
  def composeM[F[_],G[_]](F: Monad[F], G: Monad[G], T: Traverse[G]): Monad[({type f[x] = F[G[x]]})#f] = {
    new Monad[({type f[x] = F[G[x]]})#f] {
      override def unit[A](a: => A) = F.unit(G.unit(a))
      
      override def flatMap[A,B](fga: F[G[A]])(f: A => F[G[B]]): F[G[B]] = {
        val gfgb: G[A] => G[F[G[B]]] = ga => G.map(ga)(f)
        val fggb: G[A] => F[G[G[B]]] = ga => T.sequence(gfgb(ga))(F)
        val fgb: G[A] => F[G[B]] = ga => F.map(fggb(ga))(G.join)
        
        F.flatMap(fga)(fgb)
      }
    }
  }
}

case class Id[A](value: A) extends Monad[Id] {
  def unit[A](a: => A): Id[A] = Id(a)
  
  def flatMap[A,B](id: Id[A])(f: A => Id[B]): Id[B] = f(id.value) 
}