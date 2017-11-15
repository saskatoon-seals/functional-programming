package fpinscala.errorhandling

import org.scalatest.FunSuite

class EitherSuite extends FunSuite {
  test("map should apply the function on right value") {
    assert(Right(10).map(_ + 1) == Right(11))
  }
  
  test("map should return itself when operating on left") {
    assert(Left("error").map(identity) == Left("error"))
  }
  
  test("flatMap should apply the function on right value") {
    assert(Right(10).flatMap(x => Right(x + 1)) == Right(11))
  }
  
  test("flatMap should return itself when operating on left") {
    assert(Left("error").flatMap(x => Right(x)) == Left("error"))
  }
  
  test("flatMap should return left when function returns left") {
    assert(Right(10).flatMap(x => Left("error2")) == Left("error2"))
  }
  
  test("orElse should return itself when operating on right") {
    assert(Right(10).orElse(Right(11)) == Right(10))
  }
  
  test("orElse should return alternative value when operating on left") {
    assert(Left("error").orElse(Right(11)) == Right(11))
  }
  
  test("map2 should lift an ordinary function to accept and return Either") {
    assert(Right(10).map2(Right(11))(_ * _) == Right(110))
  }
  
  test("map2 should return Left") {
    assert(Right(10).map2(Left("error"))((a, b) => a.toString() ++ b) == Left("error"))
  }
}