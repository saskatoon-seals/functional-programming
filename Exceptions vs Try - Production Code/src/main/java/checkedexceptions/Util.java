package checkedexceptions;
import java.io.IOException;
import java.util.function.Function;

import com.nextbreakpoint.Try;

public final class Util {

  private Util() {

  }

  public static <T> Function<Try<T, ?>, Try<T, IOException>> exceptionMapper() {
    return input -> {
      return input.mapper(e ->
            (e instanceof IOException) ? (IOException)e : new IOException("IO Error", e));
    };
  }
}
