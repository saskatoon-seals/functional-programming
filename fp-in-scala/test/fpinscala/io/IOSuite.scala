package fpinscala.io

import org.scalatest.FunSuite

class IOSuite extends FunSuite {
  test("test printing stuff") {
    val printSomething = IOUtil.printLine("Wentao is cool!") //IO { println("Wentao is cool!") }
    
    Interpreter.run(
        printSomething flatMap { _ => printSomething }  
    )
  }
  
  test("no stack overflow error") {
    Interpreter.run(
        IO.forever(IOUtil.printLine("Wentao is cool!"))
    )
  }
}