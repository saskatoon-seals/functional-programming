package fpinscala.functors

import org.scalatest.FunSuite

import fpinscala.functors.applicative.Applicative2;
import fpinscala.laziness.Stream

class Applicative2Suite extends FunSuite {
  test("sequencing infinite streams") {
    val stream = Applicative2.stream
    
    val s1 = Stream("A1", "A2", "A3", "A4", "A5")
    val s2 = Stream("B1", "B2", "B3", "B4", "B5")
    
    val s: Stream[List[String]] = stream.sequence(List(s1, s2))
    
    assert(
        s.take(3).toList == 
          List(List("A1", "B1"), List("A2", "B2"), List("A3", "B3"))
    ) 
  }
}