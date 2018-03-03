package fpinscala.propertytesting

import org.scalatest.FunSuite;
import fpinscala.purestate.SimpleRNG;

class SGenSuite extends FunSuite  {
  test("max for non-empty lists") {
    val smallIntGen = Gen.choose(-10, 10)
    
    //specification
    val maxProp = Prop.forAll(SGen.listOf1(smallIntGen)) { ns =>
      val max = ns.max
      !ns.exists(_ > max)
    }
    
    assert(maxProp.run(20, 10, SimpleRNG(0)) == Passed)
  }
  
  //isn't implemented correctly
  test("sorting elements in natural order") {
    val min: Integer = -10
    val smallIntGen = Gen.choose(min, 10)
    
    //missing a short circuit
    val sortedProp = Prop.forAll(SGen.listOf(smallIntGen)) { ns =>
      ns.sorted
        .foldLeft((min, true)) { (acc, curr) => acc match {
          case (left, result) => (curr, result && (left <= curr))  
        }}
        ._2
    }
    
    assert(sortedProp.run(20, 10, SimpleRNG(0)) == Passed)
  }
}