package fpinscala.parsing

import fpinscala.propertytesting.Gen
import fpinscala.propertytesting.Prop
import fpinscala.propertytesting.Prop.forAll

import scala.util.matching.Regex

trait Parsers [Parser[+_]] { self =>
  
  case class Location(input: String, offset: Int = 0) {
     def toError(msg: String): ParseError =
       throw new UnsupportedOperationException
     
  }

  case class ParseError(stack: List[(Location,String)] = List());

  //1st try: type Parser[+A] = String => Either[ParseError, A]
//  type Parser[+A] = Location => Result[A]

  trait Result[+A]
  case class Success[+A](get: A, charsConsumed: Int) extends Result[A] //needed for sequencing parsers
  case class Failure(get: ParseError) extends Result[Nothing]
  
  //------------------------------abstract & implicit operations------------------------------------
  
  //1. abstract (should be): 
  
  def run[A](parser: Parser[A])(input: String): Either[ParseError, A]// =
//    parser(Location(input, 0)) match {
//      case Success(get, charsConsumed) => Right(get)
//      case Failure(get) => Left(get)
//    }
  
  //2. implicit:
  
  implicit def regex(r: Regex): Parser[String]
  
  implicit def string(s: String): Parser[String]
  
  implicit def asStringParser[A](a: A)(implicit f: A => Parser[String]): ParserOps[String] = ParserOps(f(a))
    
  implicit def operators[A](p: Parser[A]) = ParserOps[A](p)
  
  implicit def char(c: Char): Parser[Char] = 
    string(c.toString()) map (_.charAt(0))
    
  implicit def asCharParser[A](a: A)(implicit f: A => Parser[Char]): ParserOps[Char] = ParserOps(f(a))
  
  def doubleString: Parser[String] =
    regex("[-+]?([0-9]*\\.)?[0-9]+([eE][-+]?[0-9]+)?".r)
    
  def quoted: Parser[String]
  
  //---------------------------------------primitives-----------------------------------------------
  
  //p2 executes if p1 fails
  def or[A] (p1: Parser[A], p2: Parser[A]): Parser[A]
  
  //returns the portion of the string that parser examined (parsed)
  def slice[A](p: Parser[A]): Parser[String]
  
  //context based parser
  def flatMap[A,B](p: Parser[A])(f: A => Parser[B]): Parser[B]
  
  //evaluates a parser only when needed (or is an example)
  def lazyEval[A](p: => Parser[A]): Parser[A]
  
  //----------------------------------------derived-------------------------------------------------
  
  def product[A, B] (p1: Parser[A], p2: Parser[B]): Parser[(A, B)] = 
    p1 flatMap { a => p2 map { b => (a, b) } }
  
  //parser p is repeated n times
  def listOfN[A] (n: Int, p: Parser[A]): Parser[List[A]] = { 
    if (n == 0) 
      unit(List())
    else
      map2(p, listOfN(n-1, p))(_ :: _)
  }
  
  def many[A](p: Parser[A]): Parser[List[A]] = { 
    map2(p, many(p)) { _ :: _ } | unit(List())
  }
  
  /*
   * Recognizes one or more occurrences of A in String
   * 
   * lazyEval of second argument prevents an infinite recursion
   */
  def many1[A](p: Parser[A]): Parser[List[A]] =
    map2(p, lazyEval(many(p)))(_ :: _) //parser p followed by parser many(p) if p was successful
  
  def map[A,B](p: Parser[A])(f: A => B): Parser[B] = 
    p flatMap { a => unit(f(a)) }
  
  def map2[A,B,C](pa: Parser[A], pb: Parser[B])(f: (A, B) => C): Parser[C] = 
    pa ** pb map (f.tupled) //f.tupled: (ab) => f(ab._1, ab._2)
    
  def unit[A](a: A): Parser[A] = 
    string("") map (_ => a)
    
  //Wraps `p` in start/stop delimiters.
  def surround[A](start: Parser[Any], stop: Parser[Any])(p: => Parser[A])
    
  //-----------------------------------------rest---------------------------------------------------
    
  def map2Alt1[A,B,C](pa: Parser[A], pb: Parser[B])(f: (A, B) => C): Parser[C] = 
    pa flatMap { a => pb map { b => f(a, b) } }
  
  def map2Alt2[A,B,C](pa: Parser[A], pb: Parser[B])(f: (A, B) => C): Parser[C] = 
    for {
      a <- pa
      b <- pb
    } yield f(a, b)
     
  def countChar(c: Char): Parser[Int] = {
    char(c).many map (_.size)
  }
  
  //fails if string doesn't start with char c
  def countCharPositive(c: Char): Parser[Int] = {
    char(c).many1 map (_.size)
  }
  
  def countPairChar(c1: Char, c2: Char): Parser[(Int, Int)] = {
    map2(char(c1).many, char(c2).many1)((res1, res2) => (res1.size, res2.size)) 
  }
  
  def countPairChar1(c1: Char, c2: Char): Parser[(Int, Int)] = {
    char(c1).many.slice.map(_.size) ** char(c1).many1.slice.map(_.size)
  }
  
  //needs checking!
  def contextSensitiveParser(): Parser[String] = {
    for {
      number <- "^[0-9]+".r
      result <- listOfN(number.toInt, char('a')) map { list => list.toString() }
    } yield result
  }
  
  //------------------------------------------------------------------------------------------------
  
  case class ParserOps[A](p: Parser[A]) {
    def | [B >: A] (p2: Parser[B]): Parser[B] = self.or(p, p2) //method on a trait (self)
    def or[B >: A](p2: => Parser[B]): Parser[B] = self.or(p, p2)
    
    def many[AA >: A]: Parser[List[AA]] = self.many(p)
    def many1[AA >: A]: Parser[List[AA]] = self.many1(p)
    def map[B](f: A => B): Parser[B] = self.map(p)(f)
    def slice[A](): Parser[String] = self.slice(p) 
    
    def **[B] (p2: Parser[B]): Parser[(A,B)] = self.product(p, p2)
    def product[B] (p2: Parser[B]): Parser[(A,B)] = self.product(p, p2)
    
    def flatMap[B](f: A => Parser[B]): Parser[B] = self.flatMap(p)(f)
  }
  
  object Laws {
    //p1 == p2
    def equal[A](p1: Parser[A], p2: Parser[A])(input: Gen[String]): Prop =
      forAll(input)(in => run(p1)(in) == run(p2)(in))
      
    //map(p)(identity) == p
    def mapLaw[A](p: Parser[A])(input: Gen[String]): Prop = 
      forAll(input)(in => run(p.map(identity))(in) == run(p)(in))
      
    def unitLaw[A](a: A)(input: Gen[String]): Prop = 
      forAll(input)(in => run(unit(a))(in) == Right(a)) 
      
    //laws for product: associativity: ((A, B), C) ~= (A, (B, C))
    def productAssociativity[A,B,C](pa: Parser[A], pb: Parser[B], pc: Parser[C])(input: Gen[String]): Prop = 
      forAll(input)(in => run((pa ** pb) ** pc)(in) == run(pa ** (pb ** pc))(in))
      
    def productMapRelationship[A,B](pa: Parser[A], pb: Parser[B])(input: Gen[String]): Prop = 
      forAll(input)(in => run(pa ** pb)(in) == run(pa.map(a => pb.map(b => (a, b))))(in))
      
    //mapping before or after the product is irrelevant
    def productMapRelationship1[A,B,C,D](pa: Parser[A], pb: Parser[B], f: A => C, g: B => D)(input: Gen[String]): Prop = 
      forAll(input)(in => run(pa.map(f) ** pb.map(g))(in) == run(pa ** pb map { case (a, b) => (f(a), g(b)) })(in)) 
  }  
}
