package fpinscala.streamprocessing

import org.scalatest.FunSuite

class ProcessSuite extends FunSuite {
  test("counts the elements in the input - in a scan-like fashion") {
    val counterProcess: Process1[String, Int] = Process1.count
    
    //countProcess(..) is a function application that calls into apply method - process driver
    
    assert(
        counterProcess(Stream("Ales", "Maja", "Luka")).toList
        == List(1, 2, 3)
    )
  }
  
  test("calculates the running average") {
    assert(
      Process1.runningAverage(Stream(1.0, 2.0, 3.0, 4.0)).toList
      == List(1.0, 1.5, 2.0, 2.5)
    )
  }
  
  test("count is equal to count1") {
    val input = Stream("Ales", "Maja", "Luka")
    
    assert(Process1.count(input).toList == Process1.count1(input).toList)
  }
  
  test("sum") {
    val process: Process1[Int, Int] = Process1.sum 
    
    assert(
        process(Stream(1, 2, 3)).toList
        == List(1, 3, 6)
    )
  }
  
  test("exists1 halts and yields the final result only") {
    assert(
      Process1.exists1[Int](_ % 2 == 0)(Stream(1, 3, 5, 6, 7)).toList
      == List(true)
    )
  }
  
  test("exists2 halts and yields all intermediate results") {
    assert(
      Process1.exists2[Int](_ % 2 == 0)(Stream(1, 3, 5, 6, 7)).toList
      == List(false, false, false, true)
    )
  }
  
  test("exists3 doesn't halt and yields all intermediate results") {
    assert(
      Process1.exists3[Int](_ % 2 == 0)(Stream(1, 3, 5, 6, 7)).toList
      == List(false, false, false, true, true)
    )
  }
  
  test("fusing processes into a pipeline") {
//    val pipeline: Process[Int, Boolean] = Process.count |> Process.exists1(_ > 10)
//    
//    assert(
//      pipeline(Stream.range(1, 11)).toList
//      == List(true)
//    )
  }
  
  test("read a file, convert values and write to a new file") {
    def toCelsious(fahrenheit: Double): Double = 
      (5.0 / 9.0) * (fahrenheit - 32.0)

    import Process1._, fpinscala.io.IO, fpinscala.io.Interpreter
      
    //how nicely types align and the type system guarantees the safety! 
    val readIo: IO[List[String]] = 
      processFile(
          new java.io.File("/tmp/fahrenheit.txt"), 
          filter[String](_.nonEmpty) map (_.toDouble) map (toCelsious) map (_.toString), //converting pipeline 
          List[String]()
      )((acc: List[String], elem: String) => elem :: acc) //binary combiner (like in foldLeft)
      .map(_.reverse)
    
    val writer = new java.io.BufferedWriter(new java.io.FileWriter("/tmp/celsious.txt", false));
    
    val writeIo: IO[Unit] = readIo.flatMap(
      lines => IO { lines.foreach(line => writer.append(line + "\n")); writer.close() }    
    )
      
    Interpreter.run(writeIo)
  }
}