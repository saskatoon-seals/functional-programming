package fpinscala.parsing

/*
 * A different package must provide an implementation of all the functions below
 * 
 * We're deferring picking selecting the representation of:
 *  - ParserError
 *  - Parser[+_] 
 * 
 * @param Parser[+_] - covariant type constructor (wrapper of an "inner" type parameter)
 */
trait Parsers[ParserError, Parser[+_]] { self =>
  //executes the parser against a text input that either produces a result of type A or ErrorMsg
  def run[A](parser: Parser[A])(input: String): Either[ParserError, A]
  
  /*
   * Method to create a parser that knows how to parse a specific character, e.g.: 'w'
   * 
   * It needs a special constructor that is injected into Parsers. It is applied in this method to
   * Char.
   * 
   * Function (its implementation) must satisfy the law:
   *  - forall c: run(charParser(c))(c.toString) == Right(c)
   */
  def charParser(c: Char): Parser[Char]
  
  /*
   * Knows how to parse a specific string, e.g.: "Wentao" (but other strings produce errors)
   * 
   * Function (its implementation) must satisfy the law:
   *  - forall s: run(stringParser(s))(s) == Right(s)
   */
  implicit def string(s: String): Parser[String]
  
  /*
   * With this function Scala can now automatically promote a String to a Parser.
   * 
   * string function above is the one passed in asStringParser as f. 
   * In this case a: A is simply takes the type String
   */
  implicit def asStringParser[A](a: A)(implicit f: A => Parser[String]): ParserOps[String] = 
    ParserOps(f(a))
    
  implicit def operators[A](p: Parser[A]) = ParserOps[A](p)
  
  /*
   * Creates a composite parser
   * 
   * can recognize either p1's or p2's value
   */
  def or[A] (p1: Parser[A], p2: Parser[A]): Parser[A]
  
  //Recognizes n repetitions of A in an input
  def listOfN[A] (n: Int, p: Parser[A]): Parser[List[A]]
  
  /*
   * Above we've defined different combinators.
   * 
   * TODO:
   *  1. Examine a few more simple use cases
   *  2. Refine the algebra above into a minimal set of primitives
   *  3. Define more general laws for the algebra
   */
  
  case class ParserOps[A](p: Parser[A]) {
    def | [B>:A] (p2: Parser[B]): Parser[B] = self.or(p, p2) //method on a trait (self)
    def or[B >: A](p2: => Parser[B]): Parser[B] = self.or(p, p2)
  }
}
