package fpinscala.monads

case class Reader[R, A](run: R => A) 

object Reader {
  def readerMonad[R] = new Monad[({type m[a] = Reader[R,a]}) # m] {
    def unit[A](a: => A): Reader[R, A] = Reader(_ => a)
    
    //"r" is a state that is passed through both inner and outer reader and is therefore read-only
    def flatMap[A,B](ra: Reader[R,A])(f: A => Reader[R,B]): Reader[R,B] = {
      Reader(r => {
        val a = ra.run(r)
        val rb = f(a)
        
        rb.run(r)
      })
    }
  }
  
  //primitive operation - what is my r argument
  def ask[R]: Reader[R, R] = Reader(r => r)
}