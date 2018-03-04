package fpinscala.parsing

import scala.util.parsing.combinator.{Parsers => _}

import org.scalatest.FunSuite

class ParsersSuite extends FunSuite {
  val parsers: Parsers[String, Parser] = null
  
  test("char parser recognizes specific chars") {
    val char: Char = 'w'
    val charParser: Parser[Char] = parsers.charParser(char)
    
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
  
  //just as an example
  trait Parser[+A];
}