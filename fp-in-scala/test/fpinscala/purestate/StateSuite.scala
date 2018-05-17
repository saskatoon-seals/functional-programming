package fpinscala.purestate

import org.scalatest.FunSuite;

class StateSuite extends FunSuite {
  test("fibonacci 1") {
    val action = new State[(Int, Int), Int](s => {
      val x0 = s._1
      val x1 = s._2
      
      val x2 = x0 + x1
      
      (x2, (x1, x2))
    })
    
    val fib5: Int = action //1st (1, 2)
      .map2(action)((a, b) => b) //2nd (2, 3)
      .map2(action)((a, b) => b) //3rd (3, 5)
      .map2(action)((a, b) => b) //4th (5, 8)
      .run((1, 1)) //0th
      ._1
      
    assert(fib5 == 8)
  }
  
  test("fibonacci - with flatMap") {
    //How can I create a state action with unit only?
    val action = new State[(Int, Int), Int](s => {
      val x0 = s._1
      val x1 = s._2
      
      val x2 = x0 + x1
      
      (x2, (x1, x2))
    })
    
    //I know how to bind state actions to form a chain of actions
    val actions = action flatMap {_ => action} flatMap {_ => action} flatMap {_ => action}
      
    assert(actions.run((1, 1))._1 == 8)   
    
    //How can a state action become a monad?
  }
}