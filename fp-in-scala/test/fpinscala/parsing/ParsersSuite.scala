package fpinscala.parsing

import scala.util.parsing.combinator.{Parsers => _}

import org.scalatest.FunSuite

class ParsersSuite extends FunSuite {
  //just as an example
  trait Parser[+A];
  val parsers: Parsers[Parser] = null
  
  test("char parser recognizes specific chars") {
    val char: Char = 'w'
    val charParser: Parser[Char] = parsers.char(char)
    
    //law - not expressed as a property i.e. forall chars
    assert(parsers.run(charParser)(char.toString()) == Right(char))
  }
  
  test("string parser recognizes specific strings") {
    val string: String = "Wentao"
    val stringParser: Parser[String] = parsers.string(string)
    
    //law - not expressed as a property i.e. forall strings
    assert(parsers.run(stringParser)(string) == Right(string))
  }
  
  test("or can recognize [abra] or [cadabra] within abracadabra") {
    //Prepare
    val s1 = "abra"
    val s2 = "cadabra"
    
    val p1 = parsers.string(s1)
    val p2 = parsers.string(s2)
    
    //Execute
    val p: Parser[String] = parsers.or(p1, p2)
    
    //Verify
    assert(parsers.run(p)(s1) == Right(s1)) //first sub-parser does its job
    assert(parsers.run(p)(s2) == Right(s2)) //second sub-parser does its job
  }
  
  test("can recognize abra or cadabra with implicits") {
    import parsers._
    
    val s1 = "abra"
    val s2 = "cadabra"
    
    val p: Parser[String] = s1 | s2 //using of the implicits (asStringParser is called)
    
    assert(parsers.run(p)(s1) == Right(s1)) 
    assert(parsers.run(p)(s2) == Right(s2)) 
  }
  
  test("more playing with implicits") {
    import parsers._
    
    val s: String = "abracadabra"
    
    val p: Parser[String] = "abracadabra" //implicit conversion string("abracadabra")
    
    assert(parsers.run(p)(s) == Right(s))
  }
  
  test("listOfN can recognize repeated patterns (but nothing more!)") {
    import parsers._
    
    val p: Parser[List[String]] = listOfN(3, "ab" | "cad")
    
    assert(parsers.run(p)("ababcad") == Right("abracadabra"))
    assert(parsers.run(p)("cadabab") == Right("cadabab"))
    assert(parsers.run(p)("cadcadcad") == Right("cadcadcad"))
    assert(parsers.run(p)("ababab") == Right("ababab"))
  }
  
  test("countChar multiple laws") {
    import parsers._
    
    val p = countChar('a')
    
    //string that starts with 'a'
    assert(parsers.run(p)("abcdef") == Right(1))
    
    //string that doesn't start with 'a'
    assert(parsers.run(p)("fa") == Right(1))
    
    //=> the laws above say that the starting character isn't important
    
    assert(parsers.run(p)("b123") == Right(0))
  }
  
  test("countCharPositive multiple laws") {
    val p = parsers.countCharPositive('a')
    
    //even though 'a' appeared once
    assert(parsers.run(p)("bca") == Left("String must start with 'a'"))
    
    //string started with 'a'
    assert(parsers.run(p)("abca") == Right(2))
  }
  
  test("countPairChar multiple laws") {
    val p = parsers.countPairChar('a', 'b')
    
    assert(parsers.run(p)("bbb") == Right((0, 3)))
    assert(parsers.run(p)("aaaab") == Right((4, 1)))
    
    //string doesn't have to start with 'a'
    assert(parsers.run(p)("gaaaab") == Right((4, 1)))
    
    assert(parsers.run(p)("aaa") == Left("There must be at least one 'b'"))
  }
}