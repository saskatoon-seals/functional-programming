package trymonad;
import java.io.IOException;
import java.util.function.Function;

import com.nextbreakpoint.Try;

public final class Util {

  private Util() {

  }

  /**
   * Constructs a function that will map the generic exception type to a specific one 
   * for a try monad.
   * 
   * @return exception mapping function with parameterized result type
   */
  public static <T> Function<Try<T, ? extends Exception>, Try<T, IOException>> exceptionMapper() {
    return (Try<T, ? extends Exception> input) -> {
      return input.mapper(e ->
            (e instanceof IOException) ? (IOException)e : new IOException("IO Error", e));
    };
  }
}
