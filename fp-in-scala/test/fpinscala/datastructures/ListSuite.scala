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
  
  test("Reverse should reverse a list") {
    assert(List.reverse(List(1, 2, 3)) == List(3, 2, 1))
  }
  
  test("Reverse1 should reverse a list") {
    assert(List.reverse1(List(1, 2, 3)) == List(3, 2, 1))
  }
  
  test("Reverse2 should reverse a list") {
    assert(List.reverse2(List(1, 2, 3)) == List(3, 2, 1))
  }
}