package fpinscala.propertytesting

import fpinscala.purestate.SimpleRNG
import fpinscala.purestate.RNG
import fpinscala.propertytesting.Prop._
import fpinscala.laziness.Stream

sealed trait Result {
  def isFalsified: Boolean
}

case object Passed extends Result {
  def isFalsified = false
}

case class Falsified(failure: FailedCase, success: SuccessCount) extends Result {
  def isFalsified = true
}

//single run
case object Proved extends Result {
    def isFalsified = false
}

case class Prop(run: (MaxSize, TestCases, RNG) => Result) {
  //compose properties
  def &&(p: Prop): Prop = Prop {
    (max, num, rng) => run(max, num, rng) match {
      case Passed => p.run(max, num, rng)
      case res => res
    }
  }
  
  // In case of failure, run the other prop.
  def ||(p: Prop): Prop = Prop {
    (max, num, rng) => run(max, num, rng) match {
      case Falsified(msg, _) => p.tag(msg).run(max, num, rng)
      case res0 => res0
    }
  }
  
  def tag(msg0: FailedCase): Prop = Prop {
    (max, num, rng) => run(max, num, rng) match {
      case Falsified(msg1, s1) => Falsified(msg0 + "\n" + msg1, s1) 
      case res0 => res0
    }
  }
}

object Prop {
  type FailedCase = String //msg describing the reason for failure
  type SuccessCount = Int  //how many tests passed until the first failure (in the case of &&)
  type TestCases = Int     //number of tests to run
  type MaxSize = Int       //maximum size of the underlying generator (input)
  
  def randomStream[A](g: Gen[A])(rng: RNG): Stream[A] =
    Stream.unfold(rng)(rng => Some(g.next.run(rng)))
    
  def buildMsg[A](s: A, e: Exception): String =
    s"test case: $s\n" +
      s"generated an exception: ${e.getMessage}\n" +
      s"stack trace:\n ${e.getStackTrace.mkString("\n")}"
  
  //The 1st implementation
  def forAll[A](as: Gen[A])(f: A => Boolean): Prop = Prop { (max, n, rng) =>
    randomStream(as)(rng)
      .zip(Stream.from(0))
      .take(n)
      .map { case (a, i) =>
        try {
          if (f(a)) Passed else Falsified(a.toString, i)
        } catch { 
          case e: Exception => Falsified(buildMsg(a, e), i) 
        }
      }
      .find(_.isFalsified)
      .getOrElse(Passed)
}
  
  //The 2nd implementation that works with sized generators and MAX size
  def forAll[A](g: SGen[A])(f: A => Boolean): Prop = 
    forAll(g.forSize)(f)
    
  //Generating test cases up to a maximum size
  def forAll[A](g: Int => Gen[A])(f: A => Boolean): Prop = Prop {
    (max, n, rng) => 
      val casesPerSize = (n + (max - 1)) / max
      val props: Stream[Prop] = 
        Stream.from(0).take((n min max) + 1).map(size => forAll(g(size))(f))
      val prop: Prop =
        props.map(p => Prop { (max, _, rng) => 
          p.run(max, casesPerSize, rng)
        }).toList.reduce(_ && _)
        
      prop.run(max, n, rng)
  }
  
  def run(p: Prop, maxSize: Int = 100, testCases: Int = 100, rng: RNG = SimpleRNG(System.currentTimeMillis)): Unit =
    p.run(maxSize, testCases, rng) match {
      case Falsified(msg, n) =>
        println(s"! Falsified after $n passed tests:\n $msg")
      case Passed =>
        println(s"+ OK, passed $testCases tests.")
      case Proved =>
        println(s"+ OK, proved property.")
    }
}