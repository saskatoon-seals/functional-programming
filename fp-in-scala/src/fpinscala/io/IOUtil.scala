package fpinscala.io

object IOUtil {
  import IO.Return, IO.Suspend
  
  def printLine(s: String): IO[Unit] = 
    Suspend(() => Return(println(s)))
}