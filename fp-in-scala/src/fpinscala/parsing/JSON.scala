package fpinscala.parsing

import scala.collection.immutable.Map
import scala.collection.IndexedSeq

trait JSON 
  
object JSON {
  //value types (of a key-value pair):
  case object JNull extends JSON
  case class JNumber(get: Double) extends JSON
  case class JString(get: String) extends JSON
  case class JBool(get: Boolean) extends JSON
  case class JArray(get: IndexedSeq[JSON]) extends JSON
  case class JObject(get: Map[String, JSON]) extends JSON
  
  //Parser[JSON] will take String as an input and return a syntax tree (JSON) as an output
  def jsonParser[Err, Parser[+_]](P: Parsers[Parser]): Parser[JSON] = {
    import P._
    
    string("*") flatMap { value => jObjectParser(P) } 
  }
  
  //------------------------------------helper methods----------------------------------------------

  //parse a map entry while it doesn't return Left and then return Map.empty() instead (or operation)
  def mapParser[Err, Parser[+_]](P: Parsers[Parser]): Parser[Map[String, JSON]] = {
    import P._
    
    map2(mapEntryParser(P), lazyEval(mapParser(P))) {(h, t) => t + h} | unit(Map.empty()) 
  }
  
  def mapEntryParser[Err, Parser[+_]](P: Parsers[Parser]): Parser[(String, JSON)] = {
    import P._

    quoted ** jParsers(P)
  }
  
  def arrayParser[Err, Parser[+_]](P: Parsers[Parser]): Parser[IndexedSeq[JSON]] = {
    import P._
    
    map2(jParsers(P), lazyEval(arrayParser(P))) (_ +: _) | unit(IndexedSeq.empty) 
  }
  
  def jObjectParser[Err, Parser[+_]](P: Parsers[Parser]): Parser[JSON] = {
    import P._
  
//    surround("{", "}") (
//      mapParser(P) map {JObject(_)}
//    )
    throw new UnsupportedOperationException()
  }
  
  def jArrayParser[Err, Parser[+_]](P: Parsers[Parser]): Parser[JSON] = {
    import P._
    
    regex("[*]".r) flatMap {
      _ => arrayParser(P) map { JArray(_) }
    }
  }
  
  def jStringParser[Parser[+_]](P: Parsers[Parser]): Parser[JSON] = {
    import P._
    
    quoted map { JString(_) }
  }
  
  def jNumberParser[Err, Parser[+_]](P: Parsers[Parser]): Parser[JSON] = {
    import P._
    
    doubleString map { number => JNumber(number.toDouble) }
  }
  
  def jBoolParser[Err, Parser[+_]](P: Parsers[Parser]): Parser[JSON] = {
    import P._
    
    string("true").map(_ => JBool(true)) | lazyEval(string("false") map { _ => JBool(false) })
  }
  
  def jParsers[Err, Parser[+_]](P: Parsers[Parser]): Parser[JSON] = {
    import P._
    
    jObjectParser(P) | jStringParser(P) | jNumberParser(P) | jBoolParser(P)
  }
}
