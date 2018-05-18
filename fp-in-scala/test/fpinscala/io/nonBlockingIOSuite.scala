package fpinscala.io

import org.scalatest.FunSuite

import fpinscala.io.nonBlockingIO.{Par, readPar}

class nonBlockingIOSuite extends FunSuite {
  test("test benefits of non-blocking IO source turned into a monad") {
    val source: Source = null
    
    //this is possible only because of the Free monad, while Par doesn't have to be a monad
    val chainOfIoOperations: Free[Par, Unit] = for {
      page1 <- readPar(source, 1024)
      page2 <- readPar(source, 1024)
      page3 <- readPar(source, 1024)
    } yield ()
  }   
}