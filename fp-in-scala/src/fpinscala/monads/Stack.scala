package fpinscala.monads

import fpinscala.purestate.State

object Stack {
  //type
  type Stack[A] = List[A]
  
  //primitives
  def pop[A]: State[Stack[A], A] = State({ case h :: t => (h, t) })
  
  def push[A](a: A): State[Stack[A], Unit] = State(s => ((), a :: s))
  
  //derived operations
  def popMultiple[A](s0: Stack[A], n: Int): (List[A], Stack[A]) = {
    State.sequence(
        List.fill(n)(pop: State[Stack[A], A])
    ).run(s0)
  }
}