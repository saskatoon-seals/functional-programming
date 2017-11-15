package fpinscala.errorhandling

import scala.collection.immutable.List

import org.scalatest.FunSuite

class ComputationsSuite extends FunSuite {
  test("variance returns well defined value for non-empty sequence") {
    assert(Computations.variance(List(1.0, 2.0, 3.0, 4.0)) == Some(1.25))
  }
  
  test("variance returns well None for an empty sequence") {
    assert(Computations.variance(Nil) == None)
  }
}