package fpinscala.parallelism

import java.util.concurrent.ExecutorService
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Callable

import scala.Either

//Non-blocking future
sealed trait NonBlockingFuture[A] {
  /*
   * Apply method mainly focuses on how the result (of type A) of an async task is passed to an 
   * injected callback.
   */
  private[parallelism] def apply(onSuccess: A => Unit, onError: Exception => Unit): Unit
}

object AsyncPar {
  type AsyncPar[A] = ExecutorService => NonBlockingFuture[A]  
  
  /*
   * simply invokes a callback with a task's result
   * 
   * because the task has already been evaluated prior to the call, an exception can't occur
   */
  def unit[A](a: A): AsyncPar[A] =
    es => new NonBlockingFuture[A] {
      def apply(onSuccess: A => Unit, onError: Exception => Unit): Unit = onSuccess(a) 
    }
    
  /*
   * submitting a new task to the ES which will call the callback after a subtask is done
   * 
   * creates a new future which evalues a task "a" and makes it invoke the callback after 
   * it's done. Evaluation is async.
   * 
   * es => cb => es.submit { a(es)(cb) }
   * 
   * @exception Exception - evaluating a task can throw an exception
   */
  def fork[A](a: => AsyncPar[A]): AsyncPar[A] = 
    es => new NonBlockingFuture[A] {
      def apply(onSuccess: A => Unit, onError: Exception => Unit): Unit = {
        val subtask: Unit = 
          try {
            a(es)(onSuccess, onError)
          } catch {
            case e: Exception => onError(e)
          }
        
        //side-effect and exception thrown?
        eval(es)(subtask)
      }
    }
    
  /**
   * Async task evaluation
   * 
   * It submits and evaluates a task on the executor service
   * 
   * @param es - executor service on which the tasks run
   * @param task - task to submit on the executor service
   * 
   * @exception Exception - evaluating the result of "task" can throw an exception
   */
  def eval(es: ExecutorService)(task: => Unit): Unit = 
    es.submit(
        new Callable[Unit] { 
          def call = task //call can throw an exception if task throws an exception
        }
    )
    
  def map2[A,B,C](parA: AsyncPar[A], parB: AsyncPar[B])(f: (A, B) => C): AsyncPar[C] = {
    throw new UnsupportedOperationException()
  }
  
  def run[A](es: ExecutorService)(task: AsyncPar[A]): A = {
    val ref = new AtomicReference[A]
    
    val latch = new CountDownLatch(1)
    
    val onSuccess: A => Unit = result => {
      ref.set(result); 
      latch.countDown;
    }
    
    val onError: Exception => Unit = ex => {
      println(ex)
      latch.countDown;
    }
    
    //side-effect
    task(es)(onSuccess, onError) //error can be thrown
    
    //blocks for the async task to finish
    latch.await 
    
    return ref.get
  }
}