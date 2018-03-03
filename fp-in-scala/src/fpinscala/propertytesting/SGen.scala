package fpinscala.propertytesting

/*
 * Sized Generator: takes a size and produces a generator
 * 
 * It is used for "test case minimization"
 * I.e. finding the smalles failing test 
 */
case class SGen[+A](forSize: Int => Gen[A]) {
  def map[B](f: A => B): SGen[B] = SGen(
    size => forSize(size) map (f)  
  )
    
  def map2[B, C](gen2: SGen[B])(f: (A, B) => C): SGen[C] = SGen(
    size => forSize(size) 
      .map2(gen2.forSize(size))(f)
  )
  
  def flatMap[B](f: A => SGen[B]): SGen[B] = SGen(
    size => forSize(size) flatMap { a => f(a).forSize(size) }
  )
  
  def lisfOfN(genSize: SGen[Int]): SGen[List[A]] = SGen(
    size => forSize(size) lisfOfN { genSize.forSize(size) }
  )
}

object SGen {
  //Generates lists of requested size - great use case of SGen
  def listOf[A](g: Gen[A]): SGen[List[A]] = SGen(
    size => Gen.lisfOfN(size, g)    
  )
  
  //Generates non-empty lists
  def listOf1[A](g: Gen[A]): SGen[List[A]] = SGen{
    size => Gen.lisfOfN(size max 1, g)
  }
}