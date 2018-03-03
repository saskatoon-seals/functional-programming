package fpinscala.purestate

import org.scalatest.FunSuite;

class RNGSuite extends FunSuite {
  test("generates non-negative integers") {
    val rng0: RNG = SimpleRNG(0)
    
    val (y0, rng1) = RNG.nonNegativeInt(rng0)
    val (y1, rng2) = RNG.nonNegativeInt(rng1)
    val (y2, rng3) = RNG.nonNegativeInt(rng2)
    val (y3, rng4) = RNG.nonNegativeInt(rng3)
    val (y4, rng5) = RNG.nonNegativeInt(rng4)
    val (y5, _) = RNG.nonNegativeInt(rng5)
    
    assert(0 <= y0)
    assert(0 <= y1)
    assert(0 <= y2)
    assert(0 <= y3)
    assert(0 <= y4)
    assert(0 <= y5)
  }
  
  test("generates doubles between 0 and 1") {
    val rng0: RNG = SimpleRNG(0)
    
    val (y0, rng1) = RNG.double(rng0)
    val (y1, rng2) = RNG.double(rng1)
    val (y2, rng3) = RNG.double(rng2)
    val (y3, rng4) = RNG.double(rng3)
    val (y4, rng5) = RNG.double(rng4)
    val (y5, _) = RNG.double(rng5)
    
    assert(0 <= y0 && y0 <= 1)
    assert(0 <= y1 && y1 <= 1)
    assert(0 <= y2 && y2 <= 1)
    assert(0 <= y3 && y3 <= 1)
    assert(0 <= y4 && y4 <= 1)
    assert(0 <= y5 && y5 <= 1)
  }
}