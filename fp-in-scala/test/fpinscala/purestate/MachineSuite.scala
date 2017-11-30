package fpinscala.purestate

import org.scalatest.FunSuite

class MachineSuite extends FunSuite {
  test("simulateMachine for buying 4 candies") {
    val coins = 10
    val candies = 5
    val numOfPurchases = 4
    val inputs = List(Coin, Turn, Coin, Turn, Coin, Turn, Coin, Turn)
    val machine = Machine(true, candies, coins)
    
    val result = Machine
      .simulateMachine(inputs)
      .run(machine)
      ._1
    
    assert(result == (candies - numOfPurchases, coins + numOfPurchases))
  }
  
  test("insert a coin into a locked machine should unlock it") {
    val coins = 10
    val machine = Machine(true, 5, coins)
    
    val result = machine.insertCoin()
    
    assert(result.candies == 5)
    assert(result.coins == coins + 1)
    assert(result.locked == false)
  }
  
  test("turn a knob on a unlocked machine returns candy and locks the machine") {
    val candies = 5
    val machine = Machine(false, candies, 10)
    
    val result = machine.turnKnob()
    
    assert(result.candies == candies - 1)
    assert(result.coins == 10)
    assert(result.locked == true)
  }
}