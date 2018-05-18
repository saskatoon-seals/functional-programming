package fpinscala.functors.normal

import scala.Left
import scala.Right

//F[_] is a type constructor
trait Functor[F[_]] {
  //map is parameterized on the type constructor F[_]
  def map[A,B](fa: F[A])(f: A => B): F[B]
  
  /*
   * distributes a product
   * e.g.: unzip of a list of pairs into 2 lists of the same length and ordering (structure preserving) 
   */
  def distribute[A,B](fab: F[(A, B)]): (F[A], F[B]) = 
    (map(fab)(_._1), map(fab)(_._2))
    
  /*
   * distributes a coproduct
   * e.g.: receiving one of the generators and producing a generator of one of the types
   */
  def codistribute[A,B](fab: Either[F[A], F[B]]): F[Either[A, B]] = fab match {
    case Left(fa) => map(fa)(Left(_))
    case Right(fb) => map(fb)(Right(_))
  }
}

object Functor {
  def createList = new Functor[List] {
    def map[A,B](as: List[A])(f: A => B) = as map f
  }
}