package fpinscala.scopingeffects

import org.scalatest.FunSuite

class STSuite extends FunSuite {
  test("swap values") {
    val x: ST[Nothing, (Int, Int)] = for {
      //r1 is of type STRef[Nothing, Int] because of unwrapping of the for comprehension
      //STRef[Nothing,Int](1) returns ST[Nothing, STRef[Nothing, Int]]
      r1 <- STRef[Nothing,Int](1) //(1) calls the apply method
      r2 <- STRef[Nothing,Int](2)
      
      x <- r1.read
      y <- r2.read
      
      _ <- r1.write(y + 1)
      _ <- r2.write(x + 1)
      
      a <- r1.read
      b <- r2.read
    } yield (a, b)
    
    //can't call x.run(Nothing) because run is protected on ST
    
    //also it's not safe to run ST[S, STRef[S, Int]] - it returns a mutable reference
  }
  
  //RunnableST doesn't require hardcoding "passing of the token"
  
  test("swap values without initial token (state)") {
    val p = new RunnableST[(Int, Int)] {
      def apply[S] = for {
        //mutable variables r1 and r2 can't be observer outside of this function:
        r1 <- STRef(1) //r1 is of type STRef[S, Int] where it doesn't matter what S is 
        r2 <- STRef(2)
        
        x <- r1.read
        y <- r2.read
        
        _ <- r1.write(y + 1)
        _ <- r2.write(x + 1)
        
        a <- r1.read
        b <- r2.read
      } yield (a, b)  
    }
    
    //separate runner was needed, that's why "run" was protected on ST
    val result: (Int, Int) = ST.runST(p)
  }
}
