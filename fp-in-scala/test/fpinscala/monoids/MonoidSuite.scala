package fpinscala.monoids

import org.scalatest.FunSuite;

class MonoidSuite extends FunSuite {
  test("isOrdered returns true for ordered list") {
    assert(       
        Monoid.isOrdered(
            IndexedSeq(1, 2, 3, 4, 5, 6, 7, 8, 9, 10), 
            (a: Int, b: Int) => a <= b
        )
    )
  }
  
  test("isOrdered returns false for unordered list 1") {
    assert(       
        Monoid.isOrdered(
            IndexedSeq(1, 2, 215, 3, 4, 5, 6, 7, 8, 9, 10), 
            (a: Int, b: Int) => a <= b
        ) == false
    )
  }
  
  test("isOrdered returns false for unordered list 2") {
    assert(       
        Monoid.isOrdered(
            IndexedSeq(1, 2, 3, 4, 5, 6, 7, 8, 9, 215, 10), 
            (a: Int, b: Int) => a <= b
        ) == false
    )
  }
  
  test("countWords1 - counts words in a text") {
    assert(Monoid.countWords("lor sit amet, ") == 3)
  }
  
  test("counts words in a text 1") {
    assert(Monoid.countWords("lorem ipsum dolor sit amet, ") == 5)
  }
  
  test("counts words in a text 2") {
    assert(Monoid.countWords("Ales je car za lizat skoljke. Gomila je pes!") == 9)
  }
}