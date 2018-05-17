package fpinscala.purestate

//State transition object
case class State[S, +A](run: S => (A, S)) {
  def map[B](f: A => B): State[S, B] = 
    State(s0 => {
      val (a, s1) = run(s0)
      
      (f(a), s1)
    })
    
  def map2[B, C](sb: State[S, B])(f: (A, B) => C): State[S, C] = 
    State(s0 => {
      val (a, s1) = run(s0)
      val (b, s2) = sb.run(s1)
      
      (f(a, b), s2)
    })
    
  //passes both a and s1 along to the second state action
  def flatMap[B](f: A => State[S, B]): State[S, B] = 
    State(s0 => {
      val (a, s1) = run(s0)
      val (b, s2) = f(a).run(s1)
      
      (b, s2)
    })
}

object State {
  def getState[S] = State((s: S) => (s, s))
  
  def setState[S](s: S): State[S, Unit] = State(_ => ((), s))
  
  //creates a pure state computation object
  def unit[S, A](a: A): State[S, A] =
    State(s => (a, s))
  
  def sequence[S, A](fs: List[State[S, A]]): State[S, List[A]] = fs match {
    case h :: t => {
      State(s0 => {
        val (a, s1) = h.run(s0)
        val (b, s2) = sequence(t).run(s1)
        
        (a :: b, s2)
      })
    }
    case _ => unit(List())
  }
  
  def sequence1[S, A](fs: List[State[S, A]]): State[S, List[A]] = 
    fs.foldRight (unit[S, List[A]](List())) ((h, t) => h.map2(t)(_ :: _))
}