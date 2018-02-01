package fpinscala.parallelism

import fpinscala.parallelism.Par.lazyUnit;
import fpinscala.parallelism.Par.fork;
import fpinscala.parallelism.Par.Par;

import org.scalatest.FunSuite;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

class ParSuite extends FunSuite {
  test("divAndConq should return max value calculated in parallel") {
    val es: ExecutorService = Executors.newFixedThreadPool(1)
    
    assert(
        (Par.reduce(List(1, 8, 3, 5, 2), 0)(_ max _))(es).get
        == 8
    )
  }
  
  /*
   * 1. async task waits for the available thread to execute the task on.
   * 2. the outer fork executes it's task taking the available thread
   * 3. the outer task is waiting for the inner task which needs the resource the outer task
   *    is using => deadlock
   */
  test("fork results in a deadlock") {
    //Prepare
    val es: ExecutorService = Executors.newFixedThreadPool(1)
    val asyncTask: Par[Int] = lazyUnit({
      println("In the middle of execution")
      314
    })
    
    //Execute
    println("Before execution")
    val result: Int = Par.fork1(asyncTask)(es).get
    println("After execution")
    
    //"Verify" deadlock
    println(result)
  }
}