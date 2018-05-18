package fpinscala.io

import java.util.concurrent.ExecutorService

trait Source {
  def readBytes(
      numBytes: Int,
      callback: Either[Throwable, Array[Byte]] => Unit
  ):Unit
}

trait Future[+A] {
  import nonBlockingIO.Callback
  
  //abstract method
  def apply(callback: Callback[A]): Unit
}

object nonBlockingIO {
  /*********Types**********/

  //contravariance
  type Callback[-A] = A => Unit
  
  //covariance
  type Par[+A] = ExecutorService => Future[A]
  
  /*******Functions********/
  
  def asyncPar[A](run: Callback[A] => Unit): Par[A] = es => new Future[A] {
    def apply(callback: Callback[A]): Unit = run(callback)
  }

  //factory (wrapping) methods (for the purpose of having a compositional monadic interface)
  def nonBlockingRead(source: Source, numBytes: Int) = asyncPar[Either[Throwable, Array[Byte]]](
    callback => source.readBytes(numBytes, callback)  
  )
  
  def readPar(source: Source, numBytes: Int) = Free.Suspend(
    () => nonBlockingRead(source, numBytes)
  )
}