package fpinscala.purestate

import scala.annotation.tailrec

sealed trait Input
case object Coin extends Input
case object Turn extends Input

case class Machine(locked: Boolean, candies: Int, coins: Int) {
  def insertCoin(): Machine = { 
    if (this.locked && this.candies > 0) 
      //unlocks the machine and adds a coin
      Machine(false, this.candies, this.coins+1)
    else
      this
  }
  
  def turnKnob(): Machine = {
    if (!this.locked && this.candies > 0)
      //dispenses a candy and locks the machine
      Machine(true, this.candies-1, this.coins)
    else
      this
  }
}

object Machine {
  type MachineAction = State[Machine, (Int, Int)]
  
  def simulateMachine(inputs: List[Input]): MachineAction = {
    val machineActions = inputs.map(input => Machine.createAction(input))
    
    State.sequence(machineActions)
         .map(values => values.last)
  }
  
  def createAction(input: Input): MachineAction = input match {
    case Coin => toAction(_.insertCoin())
    case Turn => toAction(_.turnKnob())
  }
  
  def toAction(f: Machine => Machine): MachineAction = {
    def packResults(m: Machine): ((Int, Int), Machine) = 
      ((m.candies, m.coins), m)
    
    State(packResults _ compose f)
  }
}
