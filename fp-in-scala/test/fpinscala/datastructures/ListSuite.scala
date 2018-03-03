package fpinscala.datastructures

import org.scalatest.FunSuite

class ListSuite extends FunSuite {
  test("Apply method creates a list of two elements") {
    assert(List(1, 2) == Cons(1, Cons(2, Nil)))
  }
  
  test("Apply can create an empty list") {
    assert(List() == Nil)
  }
  
  test("Sum should return a sum of numbers for a non-empty list") {
    assert(List.sum(List(1, 2, 3, 4)) == 10)
  }
  
  test("Sum1 should return a sum of numbers for a non-empty list") {
    assert(List.sum1(List(1, 2, 3, 4)) == 10)
  }
  
  test("Product should return a product of numbers for a non-empty list") {
    assert(List.product(List(1.0, 2.0, 3.0, 4.0)) == 24.0)
  }
  
  test("Product1 should return a product of numbers for a non-empty list") {
    assert(List.product1(List(1.0, 2.0, 3.0, 4.0)) == 24.0)
  }
  
  test("Drop should drop first and second element") {
    assert(List.drop(List("A", "B", "C"), 2) == Cons("C", Nil))
  }
  
  test("DropWhile should drop elements smaller than 10") {
    val limit = 10
    val actual = List.dropWhile(List(1, 4, 7, 9, 10, 11))(x => x < limit) 
    
    assert(actual == List(10, 11))
  }
  
  test("Init should return list without it's last element") {
    assert(List.init(List("A", "B", "C")) == List("A", "B"))
  }
  
  test("Init should return singleton list") {
    assert(List.init(List("A", "C")) == List("A"))
  }
  
  test("Init should return empty list for a list with a single element") {
    assert(List.init(List("A")) == Nil)
  }
  
  test("Init should throw an exception for an empty list") {
    assertThrows[NotImplementedError] {
      List.init(Nil)
    }
  }
  
  test("Length returns the number of elements in a list") {
    assert(List.length(List(1, 2)) == 2)
  }
  
  test("Length1 returns the number of elements in a list") {
    assert(List.length1(List(1, 2)) == 2)
  }
  
  test("Append should append two lists") {
    assert(List.append(List(1, 2), List(3, 4)) == List(1, 2, 3, 4))
  }
  
  test("Append1 should append two lists") {
    assert(List.append1(List(1, 2), List(3, 4)) == List(1, 2, 3, 4))
  }
  
  test("Append2 should append two lists") {
    assert(List.append2(List(1, 2), List(3, 4)) == List(1, 2, 3, 4))
  }
  
  test("Reverse should reverse a list") {
    assert(List.reverse(List(1, 2, 3)) == List(3, 2, 1))
  }
  
  test("Reverse1 should reverse a list") {
    assert(List.reverse1(List(1, 2, 3)) == List(3, 2, 1))
  }
  
  test("Reverse2 should reverse a list") {
    assert(List.reverse2(List(1, 2, 3)) == List(3, 2, 1))
  }
  
  test("Flatten should concatenate three lists") {
    assert(List.flatten(List(List(1, 2, 3), List(4, 5), List(6))) == List(1, 2, 3, 4, 5, 6))
  }
  
  test("AddNum should add number to every element") {
    assert(List.addNum(List(4, 11), 1) == List(5, 12))
  }
  
  test("toString returns a list of strings") {
    assert(List.toString(List(1.1)) == List("1.1"))
  }
  
  test("map should return a list of strings for given list of integers") {
    assert(List.map(List(10, 11))(_.toString) == List("10", "11"))
  }
  
  test("filter should return only the elments satisfying a predicate") {
    assert(List.filter(List(2, 3))(x => x % 2 == 0) == List(2))
  }
  
  test("filter1 should return only the elments satisfying a predicate") {
    assert(List.filter1(List(2, 3))(x => x % 2 == 0) == List(2))
  }
  
  test("zip with (_ + _) should return an addition of list's numbers") {
    assert(List.zip(List(2, 3), List(1, 5))(_ + _) == List(3, 8))
  }
  
  test("hasSubsequence should return true if subsequence exists") {
    assert(List.hasSubsequence(List(1, 2, 3, 4), List(2, 3)) == true)
  }
  
  test("hasSubsequence should return false if subsequence doesn't exist") {
    assert(List.hasSubsequence(List(1, 2, 3, 4), List(1, 4)) == false)
  }
  
  test("hasSubsequence should return true if subsequence exists at the beginning") {
    assert(List.hasSubsequence(List(1, 2, 3, 4), List(1, 2)) == true)
  }
  
  test("startsWith1 should return false") {
    assert(List.startsWith1(List("A", "B", "C", "D"), List("A", "C", "D")) == false)
  }
}
