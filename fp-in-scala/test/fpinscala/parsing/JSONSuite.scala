package fpinscala.parsing

import org.scalatest.FunSuite

class JSONSuite extends FunSuite {
  trait Parser[+A];
  val parsers: Parsers[Parser] = null
  
  import JSON._
  
  test("parses empty json object") {
    val emptyJson = "{}"
    val jObjectResult = JObject(Map.empty)
    
    val jsonParser = JSON.jsonParser(parsers)
    
    assert(parsers.run(jsonParser)(emptyJson) == Right(jObjectResult))
  }
  
  test("parses json string") {
    val jsonTxt = """
    {
      "Company name" : "Microsoft Corporation",
      "Ticker"  : "MSFT",
      "Active"  : true,
      "Price"   : 30.66,
      "Shares outstanding" : 8.38e9,
      "Related companies" : [ "HPQ", "IBM", "YHOO", "DELL", "GOOG" ]
    }
    """
    
    print(jsonTxt)
  }
}