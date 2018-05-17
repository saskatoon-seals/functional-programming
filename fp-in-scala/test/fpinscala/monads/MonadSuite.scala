package fpinscala.monads

import fpinscala.datastructures.List
import org.scalatest.FunSuite
import fpinscala.purestate.State

class MonadSuite extends FunSuite {
  //------------------------------------list----------------------------------------
  
  test("list creation") {
    import Monad.list._
    
    val l1 = unit(1)
    val l2 = unit(2)
    
    val l3 = flatMap(l1) { x1 => 
      flatMap(l2) { x2 =>
        List(x1, x2)
      }
    }
    
    val l4 = map2(l1, l2)((a, b) => List(a, b))
    
    assert(l3 == List(1, 2) && l3 == l4)
  }
  
  test("list's flatMap doesn't result in concatenated list") {
    //type of a list is unknown at this point
    val ml = Monad.list
    
    //it becomes known only after calling a .unit(..)
    val as1 = List(1, 2)
    val as2 = List(3, 4)
    
    //[1, 3, 1, 4, 2, 3, 2, 4]
    val l3: List[Int] = ml.flatMap(as1) { a1 => 
      ml.flatMap(as2) { a2 =>
        List(a1, a2)
      }
    } 
    
    assert(l3 == List(1, 2, 3, 4))
  }
  
  test("list sequencing") {
    val ml = Monad.list
    
    val l1 = ml.unit(1)
    val l2 = ml.unit(2)
    
    val l3: List[Int] = ml.flatMap(ml.sequence(List(l1, l2)))(identity)
    
    assert(l3 == List(1, 2))
  }
  
  //------------------------------------state----------------------------------------
  
  test("state replicateM") {
    val state = Monad.state3[(Int, Int)]
    import state._
    
    val fibonacci = new State[(Int, Int), Int]({ case (x0, x1) => 
      val x2 = x0 + x1
      
      (x2, (x1, x2))
    })
    
    val results: List[Int] = replicateM(5, fibonacci)
      .run((1, 1))
      ._1
    
    assert(List.last(results) == 13)
  }
  
  test("state map2") {
    val state = Monad.state3[(Int, Int)]
    import state._
    
    val fibonacci = new State[(Int, Int), Int]({ case (x0, x1) => 
      val x2 = x0 + x1
      
      (x2, (x1, x2))
    })
    
    val result = map2(fibonacci, fibonacci)((x0, x1) => x1).run((1, 1))._1
    
    assert(result == 3)
  }
  
  test("state sequence") {
    val state = Monad.state3[(Int, Int)]
    import state._
    
    val fibonacci = new State[(Int, Int), Int]({ case (x0, x1) => 
      val x2 = x0 + x1
      
      (x2, (x1, x2))
    })
    
    val results: List[Int] = sequence(List.listOfN(fibonacci, 5))
      .run((1, 1))
      ._1
    
    assert(List.last(results) == 13)
  }
  
  //-------------------------------getState & setState----------------------------------------------
  
  test("Getting and setting the same state does nothing") {
    import State._
    
    val state: State[Unit, Unit] = getState.flatMap(s => setState(s)) 
    
    assert(state == unit(()))
    
//    for {
//      s <- State.getState
//      _ <- setState(s)
//    } yield ()
  }
  
  test("Setting and getting the state is yields the input state") {
    import State._
    
    val state: State[Int, Int] = setState(1000).flatMap(_ => getState)
    
    assert(state == unit(1000))
    
    for {
      _ <- setState(1000)
      x <- getState
    } yield x
  }
}