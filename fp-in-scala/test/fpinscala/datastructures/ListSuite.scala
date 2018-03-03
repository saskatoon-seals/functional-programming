package fpinscala.datastructures

import org.scalatest.FunSuite

class ListSuite extends FunSuite {
  test("Apply method creates a list of two elements") {
    assert(List1(1, 2) == Cons(1, Cons(2, Nil)))
  }
  
  test("Apply can create an empty list") {
    assert(List1() == Nil)
  }
  
  test("Sum should return a sum of numbers for a non-empty list") {
    assert(List1.sum(List1(1, 2, 3, 4)) == 10)
  }
  
  test("Sum1 should return a sum of numbers for a non-empty list") {
    assert(List1.sum1(List1(1, 2, 3, 4)) == 10)
  }
  
  test("Product should return a product of numbers for a non-empty list") {
    assert(List1.product(List1(1.0, 2.0, 3.0, 4.0)) == 24.0)
  }
  
  test("Product1 should return a product of numbers for a non-empty list") {
    assert(List1.product1(List1(1.0, 2.0, 3.0, 4.0)) == 24.0)
  }
  
  test("Drop should drop first and second element") {
    assert(List1.drop(List1("A", "B", "C"), 2) == Cons("C", Nil))
  }
  
  test("DropWhile should drop elements smaller than 10") {
    val limit = 10
    val actual = List1.dropWhile(List1(1, 4, 7, 9, 10, 11))(x => x < limit) 
    
    assert(actual == List1(10, 11))
  }
  
  test("Init should return list without it's last element") {
    assert(List1.init(List1("A", "B", "C")) == List1("A", "B"))
  }
  
  test("Init should return singleton list") {
    assert(List1.init(List1("A", "C")) == List1("A"))
  }
  
  test("Init should return empty list for a list with a single element") {
    assert(List1.init(List1("A")) == Nil)
  }
  
  test("Init should throw an exception for an empty list") {
    assertThrows[NotImplementedError] {
      List1.init(Nil)
    }
  }
  
  test("Length returns the number of elements in a list") {
    assert(List1.length(List1(1, 2)) == 2)
  }
  
  test("Length1 returns the number of elements in a list") {
    assert(List1.length1(List1(1, 2)) == 2)
  }
  
  test("Append should append two lists") {
    assert(List1.append(List1(1, 2), List1(3, 4)) == List1(1, 2, 3, 4))
  }
  
  test("Append1 should append two lists") {
    assert(List1.append1(List1(1, 2), List1(3, 4)) == List1(1, 2, 3, 4))
  }
  
  test("Append2 should append two lists") {
    assert(List1.append2(List1(1, 2), List1(3, 4)) == List1(1, 2, 3, 4))
  }
  
  test("Reverse should reverse a list") {
    assert(List1.reverse(List1(1, 2, 3)) == List1(3, 2, 1))
  }
  
  test("Reverse1 should reverse a list") {
    assert(List1.reverse1(List1(1, 2, 3)) == List1(3, 2, 1))
  }
  
  test("Reverse2 should reverse a list") {
    assert(List1.reverse2(List1(1, 2, 3)) == List1(3, 2, 1))
  }
  
  test("Flatten should concatenate three lists") {
    assert(List1.flatten(List1(List1(1, 2, 3), List1(4, 5), List1(6))) == List1(1, 2, 3, 4, 5, 6))
  }
  
  test("AddNum should add number to every element") {
    assert(List1.addNum(List1(4, 11), 1) == List1(5, 12))
  }
  
  test("toString returns a list of strings") {
    assert(List1.toString(List1(1.1)) == List1("1.1"))
  }
  
  test("map should return a list of strings for given list of integers") {
    assert(List1.map(List1(10, 11))(_.toString) == List1("10", "11"))
  }
  
  test("filter should return only the elments satisfying a predicate") {
    assert(List1.filter(List1(2, 3))(x => x % 2 == 0) == List1(2))
  }
  
  test("filter1 should return only the elments satisfying a predicate") {
    assert(List1.filter1(List1(2, 3))(x => x % 2 == 0) == List1(2))
  }
  
  test("zip with (_ + _) should return an addition of list's numbers") {
    assert(List1.zip(List1(2, 3), List1(1, 5))(_ + _) == List1(3, 8))
  }
  
  test("hasSubsequence should return true if subsequence exists") {
    assert(List1.hasSubsequence(List1(1, 2, 3, 4), List1(2, 3)) == true)
  }
  
  test("hasSubsequence should return false if subsequence doesn't exist") {
    assert(List1.hasSubsequence(List1(1, 2, 3, 4), List1(1, 4)) == false)
  }
  
  test("hasSubsequence should return true if subsequence exists at the beginning") {
    assert(List1.hasSubsequence(List1(1, 2, 3, 4), List1(1, 2)) == true)
  }
  
  test("startsWith1 should return false") {
    assert(List1.startsWith1(List1("A", "B", "C", "D"), List1("A", "C", "D")) == false)
  }
}
