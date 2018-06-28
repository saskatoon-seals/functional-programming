package fpinscala.scopingeffects

//State Token
sealed trait ST[S,A] { self =>
  protected def run(s: S): (A,S)
  
  def map[B](f: A => B): ST[S,B] = new ST[S,B] {
    def run(s: S) = {
      val (a, s1) = self.run(s)
      (f(a), s1)
    }
  }
  
  def flatMap[B](f: A => ST[S,B]): ST[S,B] = new ST[S,B] {
    def run(s: S) = {
      val (a, s1) = self.run(s)
      f(a).run(s1)
    }
  }
}

object ST {
  def apply[S,A](a: => A) = {
    lazy val memo = a //cache if run is called more than once
    
    new ST[S,A] {
      def run(s: S) = (memo, s)
    }
  }
  
  //() is a token of type Unit and with ._1 it's discarded (the token)
  def runST[A](runnableSt: RunnableST[A]): A = {
    //produces the state token type who's token is of type Unit
    val st: ST[Unit, A] = runnableSt.apply 
    
    //and now a token of type Unit is passed into run ("authentication" / token types match)
    st.run(())._1
  }
    
}

sealed trait STRef[S,A] {
  protected var cell: A
  
  def read: ST[S,A] = ST(cell)
  
  def write(a: => A): ST[S,Unit] = new ST[S,Unit] {
    def run(s: S) = {
      cell = a //mutation
      
      ((), s)
    }
  }
}

object STRef {
  def apply[S,A](a: A): ST[S, STRef[S,A]] = ST(new STRef[S,A] {
    var cell = a
  })
}

//actions are polymorphic in S
trait RunnableST[A] {
  //apply takes a "type S" and produces a VALUE of "type ST[S, A]" 
  //type => value
  def apply[S]: ST[S,A]
}

// Scala requires an implicit Manifest for constructing arrays.
sealed abstract class STArray[S,A](implicit manifest: Manifest[A]) {
  protected def value: Array[A]
  def size: ST[S,Int] = ST(value.size)

  // Write a value at the give index of the array
  def write(i: Int, a: A): ST[S,Unit] = new ST[S,Unit] {
    def run(s: S) = {
      value(i) = a
      ((), s)
    }
  }

  // Read the value at the given index of the array
  def read(i: Int): ST[S,A] = ST(value(i))

  // Turn the array into an immutable list
  def freeze: ST[S,List[A]] = ST(value.toList)
  
  // It implies that the array needs to be the correct size (equal to max key in Map[Int, A])
  def fill(xs: Map[Int, A]): ST[S, Unit] = {
    xs.foldRight(ST[S,Unit](())) {
      case ((index, value), st) => st flatMap (_ => this.write(index, value)) //local effects 
    }
  }
  
  def swap(i: Int, j: Int): ST[S, Unit] = for {
    valueI <- read(i)
    valueJ <- read(j)
    _ <- write(i, valueJ)
    _ <- write(j, valueI)
  } yield ()
}

object STArray {
  // Construct an array of the given size filled with the value v
  def apply[S,A:Manifest](sz: Int, v: A): ST[S, STArray[S,A]] =
    ST(new STArray[S,A] {
      lazy val value = Array.fill(sz)(v)
    })
    
  def fromList[S,A:Manifest](xs: List[A]): ST[S, STArray[S,A]] =
    ST(new STArray[S,A] {
      lazy val value = xs.toArray
  })
}

object Immutable {
  def noop[S] = ST[S,Unit](())

  def partition[S](a: STArray[S,Int], l: Int, r: Int, pivot: Int): ST[S,Int] = for {
    pivotVal <- a.read(pivot)
    _ <- a.swap(pivotVal, r)
    j <- STRef(l)
    
    //complicated stuff starts here...    
  } yield Int.MaxValue //wrong! ;)

  def qs[S](a: STArray[S,Int], l: Int, r: Int): ST[S, Unit] = if (l < r) for {
    pivot <- partition(a, l, r, (1.5 * l - 0.5*r).toInt)
    _ <- qs(a, l, pivot - 1)
    _ <- qs(a, pivot + 1, r)
  } yield () else noop[S]

  def quicksort(xs: List[Int]): List[Int] =
    if (xs.isEmpty) xs else ST.runST(new RunnableST[List[Int]] {
      def apply[S] = for {
        arr    <- STArray.fromList(xs)
        size   <- arr.size
        _      <- qs(arr, 0, size - 1)
        sorted <- arr.freeze
      } yield sorted
  })
}