package fpinscala.errorhandling

import org.scalatest.FunSuite

class OptionSuite extends FunSuite {
  //---------------------------------------trait tests----------------------------------------------
  test("Map should transform integer to string") {
    assert(Some(1).map(_.toString()) == Some("1"))
  }
  
  test("Map should transform string to integer") {
    assert(Some("1").map(_.toInt) == Some(1))
  }
  
  test("FlatMap should transform integer to string") {
    assert(Some(1).flatMap(x => Some(x.toString())) == Some("1"))
  }
  
  test("FlatMap should result in None for failing f") {
    assert(Some(1).flatMap(x => None) == None)
  }
  
  test("orElse should return itself when it's not None") {
    assert(Some(1).orElse(None) == Some(1))
  }
  
  test("orElse should return alternative value") {
    assert(None.orElse(Some(2)) == Some(2))
  }
  
  test("filter should return the same value if predicate is true") {
    assert(Some(1).filter(x => x > 0) == Some(1))
  }
  
  test("filter should return None if predicate is false") {
    assert(Some(1).filter(x => x < 0) == None)
  }
  
  //--------------------------------------object tests----------------------------------------------
  
  test("map2 should lift parameters of a given function") {
    assert(Option.map2(Some(4), Some(3))((a, b) => a * b) == Some(12))
  }
  
  test("map2 should return None if one of parameters is None") {
    assert(Option.map2(Some(4), None: Option[Int])((a, b) => a * b) == None)
  }
  
  test("sequence should return some values") {
    assert(Option.sequence(List(Some(1), Some(2))) == Some(List(1, 2)))
  }
  
  test("sequence should return None when one of values is None") {
    assert(Option.sequence(List(Some(1), None)) == None)
  }
  
  test("sequence1 should return some values") {
    assert(Option.sequence1(List(Some(1), Some(2))) == Some(List(1, 2)))
  }
  
  test("sequence1 should return None when one of values is None") {
    assert(Option.sequence1(List(Some(1), None)) == None)
  }
  
  test("sequence3 should return some values") {
    assert(Option.sequence3(List(Some(1), Some(2))) == Some(List(1, 2)))
  }
  
  test("sequence3 should return None when one of values is None") {
    assert(Option.sequence3(List(Some(1), None)) == None)
  }
  
  test("traverse should return some list of values") {
    assert(
        Option.traverse(List(1, 2, 3))(x => Some(x.toString())) 
        == Some(List("1", "2", "3"))
    )
  }
  
  test("traverse1 should return some list of values") {
    assert(
        Option.traverse1(List(1, 2, 3))(x => Some(x.toString())) 
        == Some(List("1", "2", "3"))
    )
  }
  
  test("traverse should return None") {
    assert(Option.traverse(List(1, 2, 3))(_ => None) == None)
  }
}
