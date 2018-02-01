package fpinscala.parallelism

import java.util.concurrent.TimeUnit;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.Future

/*
trait ExecutorService {
  def submit[A](a: Callable[A]): Future[A]
}

//can be though as a lazy A
trait Callable[A] { def call: A }

//handle to a (async) running computation
trait Future[A] {
  def get: A
  def getResult(timeout: Long, unit: TimeUnit): (A, Long)
  def get(timeout: Long, unit: TimeUnit): A
  def cancel(evenIfRunning: Boolean): Boolean
  def isDone: Boolean
  def isCancelled: Boolean
}
*/

object Par {
  //represent a non-running computation
  type Par[A] = ExecutorService => Future[A]
  
  def unit[A](a: A): Par[A] = (es: ExecutorService) => UnitFuture(a)
  
  /*
   * just wraps a constant into a future, but doesn't use the executor service
   * it's passed in a result of an computation, meaning no computation runs
   */
  private case class UnitFuture[A](get: A) extends Future[A] {
    def get(timeout: Long, unit: TimeUnit) = get
    def getResult(timeout: Long, unit: TimeUnit) = (get, 0)
    def cancel(evenIfRunning: Boolean) = false
    def isDone = true
    def isCancelled = false;
  }
  
  //Because of fork map2 can have strict arguments
//  def map2[A,B,C](parA: Par[A], parB: Par[B], timeout: Long, unit: TimeUnit)(f: (A, B) => C): Par[C] = 
//    es => {
//      val futureA = parA(es)
//      val futureB = parB(es)
//      
//      val (a, elapsed) = futureA.getResult(timeout, unit)
//      val b = futureB.get(timeout - elapsed, unit)
//      
//      UnitFuture(f(a, b))
//    }
    
  def map2[A,B,C](parA: Par[A], parB: Par[B])(f: (A, B) => C): Par[C] = 
    es => {
      //starts executing computations in the background
      val futureA = parA(es) 
      val futureB = parB(es) 
      
      //blocks for the results and combines them
      val c = f(
        futureA.get, 
        futureB.get
      )
      
      UnitFuture(c)
    }
  
  //Executes pa before it starts executing pb, meaning there's no parallelism?
  def map2Alt[A,B,C](pa: Par[A], pb: Par[B])(f: (A, B) => C): Par[C] = 
    flatMap(pa){ a => flatMap(pb)(b => unit(f(a, b))) }
 
  /*
   * Starts executing par in a separate logical thread
   * 
   * @param par - is lazily evaluated
   */
  def fork[A](par: => Par[A]): Par[A] = 
    executor => {
      //start executing the computation [in Java: par.get().apply(executor)]
      val future = par(executor)
      
      val blockingTask = new Callable[A] { 
        def call = future.get //blocking subtask 
      }
      
      //pass the blocking subtask to another thread so that it becomes nonblocking
      executor.submit(blockingTask)
    }
    
  def fork1[A](par: => Par[A]): Par[A] = 
    es => es.submit(
      () => par(es).get
    )
    
  def lazyUnit[A](a: => A): Par[A] = fork(unit(a))
  
  /*
   * it takes the function f and evaluates it in a background thread (async)
   * (meaning the function will execute/run in the background)
   * 
   * What does Par[A => B] mean?
   * That it will construct the function in the background but not evaluate it, which isn't very
   * useful performance wise
   */
  def asyncF[A,B](f: A => B): A => Par[B] = 
    a => lazyUnit(f(a))
    
  /*
   * combines a sequence of parallel computation into a single parallel computation that returns a
   * sequence of results 
   */
  def sequence[A](pars: List[Par[A]]): Par[List[A]] = 
    pars.foldRight (unit(Nil): Par[List[A]]) ((par, result) => map2 (par, result) (_ :: _))
    
  //Combines N parallel computations
  def parMap[A,B](as: List[A])(f: A => B): Par[List[B]] = fork { 
    sequence(as.map(asyncF(f)))
  }

  //TODO: Implement parFilter1 using sequence
  def parFilter[A](as: List[A])(p: A => Boolean): Par[List[A]] = {
    val pars: List[Par[(A, Boolean)]] = as.map(asyncF(a => (a, p(a))))

    pars.foldRight(unit(Nil): Par[List[A]])((par, result) =>
      map2 (par, result) ((h, t) => if (h._2) h._1 :: t else t)
    )
  }
  
  def reduce[A](as: Seq[A], zero: A)(f: (A, A) => A): Par[A] = {
    def go(as: Seq[A]): Par[A] = 
      if (as.length <= 1) 
        unit(as.headOption getOrElse zero)
      else {
        val (left, right) = as.splitAt(as.length / 2)
        
        map2(fork(go(left)), fork(go(right)))(f)
      }  
    
    go(as)
  }
  
  def reduce1[A,B](as: Seq[A], zero: B, g: A => B)(f: (B, B) => B): Par[B] = as match {
    case Nil => unit(zero)
    case a :: Nil => lazyUnit(g(a)) //g(a) should be lazily evaluated
    case as => {
      val toPar = (xs: Seq[A]) => fork(reduce1(xs, zero, g)(f))
      val (left, right) = as.splitAt(as.length / 2)
      
      map2(toPar(left), toPar(right))(f)
    }
  }
  
  def numWords(paragraphs: List[String], es: ExecutorService): Int = {
    val g = (paragraph: String) => paragraph.split(" ").size
    
    reduce1(paragraphs, 0, g)(_ + _)(es).get
  }
  
  def map[A,B](parA: Par[A])(f: A => B): Par[B] = 
    map2(parA, unit(()))((a, _) => f(a))
  
  def map3[A,B,C,D](parA: Par[A], parB: Par[B], parC: Par[C])(f: (A, B, C) => D): Par[D] = {
    val parAtoD: Par[A => D] = map2(parB, parC)((b, c) => a => f(a, b, c))

    map2(parA, parAtoD)((a, toD) => toD(a))
  }
  
  def map4[A,B,C,D,E](pA: Par[A], pB: Par[B], pC: Par[C], pD: Par[D])(f: (A, B, C, D) => E): Par[E] = {
    map2(map2(map2(pA, pB)((a, b) => (c: C) => (d: D) => f(a,b,c,d)), pC)(_(_)), pD)(_(_))
  }
  
  def map5[A,B,C,D,E,F](pA: Par[A], pB: Par[B], pC: Par[C], pD: Par[D], pE: Par[E])(f: (A, B, C, D, E) => F): Par[F] =
    map2(pA, map4(pB, pC, pD, pE)((b, c, d, e) => (a: A) => f(a, b, c, d, e)))((a, toF) => toF(a))
    
  //Int => Par[A]
  def choiceN[A](n: Par[Int])(choices: List[Par[A]]): Par[A] = 
    es => {
      choices(n(es).get)(es)
    }
    
  def choice1N[A](pn: Par[Int])(choices: List[Par[A]]): Par[A] =
    flatMap(pn)(n => choices(n))
  
  //Boolean => Par[A]
  def choice[A](cond: Par[Boolean])(a: Par[A], b: Par[A]) = 
    choiceN 
      { map(cond)(bool => if (bool) 1 else 0) } 
      { List(a, b) }
      
  def choice1[A](pcond: Par[Boolean])(a: Par[A], b: Par[A]) =
    flatMap(pcond)(cond => if (cond) a else b)
      
  //K => Par[K]
  def choiceMap[K,V](key: Par[K])(choices: Map[K,Par[V]]): Par[V] = 
    es => choices(key(es).get)(es)
    
  def choiceMap1[K,V](pkey: Par[K])(choices: Map[K,Par[V]]): Par[V] =
    flatMap(pkey)(key => choices(key))
    
  def flatMap[A,B](pa: Par[A])(choices: A => Par[B]): Par[B] = 
    es => {
      val a = pa(es).get
      choices(a)(es)
    }
    
  def flatMap1[A,B](pa: Par[A])(choices: A => Par[B]): Par[B] =
    join { map(pa)(a => choices(a)) }
    
  def join[A](ppa: Par[Par[A]]): Par[A] = 
    es => {
      val pa = ppa(es).get
      
      pa(es)
    }
    
  def join1[A](ppa: Par[Par[A]]): Par[A] =
    flatMap(ppa)(identity)
}