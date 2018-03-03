package fpinscala.propertytesting

import org.scalatest.FunSuite;
import fpinscala.purestate.SimpleRNG

class GenSuite extends FunSuite {
  test("choose returns integers in a given range") {
    val start = -5
    val stop = 5
    
    val gen = Gen.choose(start, stop)
    
    val (y0, rng1) = gen.next.run(SimpleRNG(0))
    val (y1, rng2) = gen.next.run(rng1)
    val (y2, rng3) = gen.next.run(rng2)
    val (y3, rng4) = gen.next.run(rng3)
    val (y4, rngstop) = gen.next.run(rng4)
    val (y5, _) = gen.next.run(rngstop)
    
    assert(start <= y0 && y0 <= stop)
    assert(start <= y1 && y1 <= stop)
    assert(start <= y2 && y2 <= stop)
    assert(start <= y3 && y3 <= stop)
    assert(start <= y4 && y4 <= stop)
    assert(start <= y5 && y5 <= stop)
  }
}