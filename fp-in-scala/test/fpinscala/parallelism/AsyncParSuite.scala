package fpinscala.parallelism

import fpinscala.parallelism.AsyncPar.AsyncPar

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

import org.scalatest.FunSuite;

//TODO: Figure out why the exception isn't thrown
class AsyncParSuite extends FunSuite {
  val es: ExecutorService = Executors.newFixedThreadPool(1)
  
  test("async task produces result - onSuccess") {
    assert(
        AsyncPar.run(es)(AsyncPar.fork(AsyncPar.unit(314)))
        == 314
    )
  }
  
  test("async task throws an exception but still finishes - onError") {
    val task: AsyncPar[Any] = AsyncPar.unit(
        throw new RuntimeException("Wentao!")
    )
    
    AsyncPar.run(es)(AsyncPar.fork(task)) //blocks
  }
}