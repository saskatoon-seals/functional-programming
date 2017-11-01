package fpinscala.datastructures

import org.scalatest.FunSuite

class TreeSuite extends FunSuite {
  test("Size should return expected size for a given tree") {
    assert(Tree.size(Branch(Leaf("valueA"), Leaf("valueB"))) == 3)
  }
  
  test("Maximum should return the largest value") {
    assert(Tree.maximum(Branch(Leaf(3), Leaf(5))) == 5) 
  }
  
  test("Depth should return the maximum depth of a tree") {
    assert(Tree.depth(Branch(Leaf("A"), Branch(Branch(Leaf("B"), Leaf("C")), Leaf("D")))) == 4)
  }
  
  test("Map should convert every string to integer") {
    assert(
        Tree.map(Branch(Leaf("10"), Branch(Leaf("1"), Leaf("-2"))))(_.toInt) 
        == Branch(Leaf(10), Branch(Leaf(1), Leaf(-2)))
    )
  }
  
  test("Size1 should return expected size for a given tree") {
    assert(Tree.size1(Branch(Leaf("valueA"), Leaf("valueB"))) == 3)
  }
  
  test("Maximum1 should return the largest value") {
    assert(Tree.maximum1(Branch(Leaf(3), Leaf(5))) == 5) 
  }
  
  test("Depth1 should return the maximum depth of a tree") {
    assert(Tree.depth1(Branch(Leaf("A"), Branch(Branch(Leaf("B"), Leaf("C")), Leaf("D")))) == 4)
  }
  
  test("Map1 should convert every string to integer") {
    assert(
        Tree.map1(Branch(Leaf("10"), Branch(Leaf("1"), Leaf("-2"))))(_.toInt) 
        == Branch(Leaf(10), Branch(Leaf(1), Leaf(-2)))
    )
  }
}
