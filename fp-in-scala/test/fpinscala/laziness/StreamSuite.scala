package fpinscala.laziness

import org.scalatest.FunSuite
import scala.util.Random

class StreamSuite extends FunSuite {
  test("headOption1 should return first element in a stream") {
    assert(Stream(1, 2, 3).headOption1 == Some(1))
  }
  
  test("headOption1 should return None for empty stream") {
    assert(Stream().headOption1 == None)
  }
  
  test("append should append two streams") {
    assert(Stream(1, 2).append(Stream(3, 4)).toList == List(1, 2, 3, 4))
  }
  
  test("flatMap success") {
    assert(
        Stream(1, 2).flatMap(x => Stream(x.toString(), x.toString())).toList
        == List("1", "1", "2", "2")
    )
  }
  
  test("fibs should return n fibonacci numbers") {
    assert(Stream.fibs().take(6).toList == List(0, 1, 1, 2, 3, 5))
  }
  
  test("unfold produces next values") {
    assert(
        Stream.unfold(0)(s => Some((s * 10, s + 1))).take(3).toList
        == List(0, 10, 20)
    )
  }
  
  test("constant1 should return indefinite stream of constants") {
    assert(Stream.constant1(5).take(2).toList == List(5, 5))
  }
  
  test("from1 should return same values as from") {
    assert(Stream.from(0).take(2).toList == Stream.from1(0).take(2).toList)
  }
  
  test("fibs1 should return same values as fibs") {
    assert(Stream.fibs1().take(10).toList == Stream.fibs().take(10).toList)
  }
  
  test("map1 should return the same result as map") {
    val x = Stream(1, 2, 3)
    val f = (x: Int) => x.toString()
    
    assert(x.map1(f).toList == x.map(f).toList)
  }
  
  test("takeWhile2 should return the same result as takeWhile") {
    val x = Stream(1, 2, 3)
    val p = (x: Int) => x < 3
    
    assert(x.takeWhile2(p).toList == x.takeWhile(p).toList)
  }
  
  test("zip should return a sum per element of two equal long streams") {
    assert(Stream.zip(Stream(1, 2), Stream(3, 4))(_ + _).toList == List(4, 6)) 
  }
  
  test("zip should return a sum per element when first stream is shorter") {
    assert(Stream.zip(Stream(1, 2), Stream(3, 4, 5))(_ + _).toList == List(4, 6)) 
  }
  
  test("zip should return a sum per element when first stream is longer") {
    assert(Stream.zip(Stream(1, 2, 5), Stream(3, 4))(_ + _).toList == List(4, 6)) 
  }
  
  test("hasSubsequence should return true if stream starts with given subsequence") {
    assert(Stream("A", "B", "C", "D").hasSubsequence(Stream("A", "B", "C")) == true)
  }
  
  test("hasSubsequence should return true if stream ends with given subsequence") {
    assert(Stream("A", "B", "C", "D").hasSubsequence(Stream("B", "C", "D")) == true)
  }
  
  test("hasSubsequence should return true if given subsequence is in the middle of the stream") {
    assert(Stream("A", "B", "C", "D").hasSubsequence(Stream("B", "C")) == true)
  }
  
  test("hasSubsequence should return false if stream doesn't contain the given subsequence") {
    assert(Stream("A", "B", "C", "D").hasSubsequence(Stream("A", "B", "D")) == false)
  }
  
  test("startsWith should return true if stream starts with given subsequence") {
    assert(Stream("A", "B", "C", "D").startsWith(Stream("A", "B", "C")) == true)
  }
  
  test("startsWith should return false if stream ends with a given subsequence") {
    assert(Stream("A", "B", "C", "D").startsWith(Stream("B", "C", "D")) == false)
  }
  
  test("startsWith should return false if given subsequence is in the middle of the stream") {
    assert(Stream("A", "B", "C", "D").startsWith(Stream("B", "C")) == false)
  }
  
  test("startsWith1 should return false") {
    assert(Stream("A", "B", "C", "D").startsWith1(Stream("A", "C")) == false)
  }
  
  test("startsWith should return false for an empty stream") {
    assert(Stream().startsWith(Stream("B", "C")) == false)
  }
  
  test("tails should return a stream of streams") {
    val actual = Stream(1, 2, 3).tails.flatMap(identity).toList
    val expected = Stream(Stream(1, 2, 3), Stream(2, 3), Stream(3), Stream()).flatMap(identity).toList 
    
    assert(actual == expected)
  }
  
  test("tails1 should return a stream of streams") {
    val actual = Stream(1, 2, 3).tails1.flatMap(identity).toList
    val expected = Stream(Stream(1, 2, 3), Stream(2, 3), Stream(3), Stream()).flatMap(identity).toList 
    
    assert(actual == expected)
  }
  
  test("scanRight should return a stream of sums") {
    assert(Stream(1, 2, 3).scanRight(0)(_ + _).toList == List(6, 5, 3, 0))
  }
  
  test("scanRight should return a stream of sums with a non-zero unit") {
    assert(Stream(1, 2, 3).scanRight(1)(_ + _).toList == List(7, 6, 4, 1))
  }
}