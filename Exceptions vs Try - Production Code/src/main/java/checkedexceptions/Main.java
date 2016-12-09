package checkedexceptions;
import java.util.stream.IntStream;

public class Main {
  public static final int MAX_ADDRESS = 100;

  private static long update(IntStream stream) {
//    return stream.mapToObj(RegisterAccess::read)
//                 .filter(result -> result.flatMap(RegisterAccess::write)
//                                         .isPresent())
//                 .count();
      return 0;
  };

  public static void main(String... args) {
    System.out.println("Number of successful updates is: " +
                       update(IntStream.range(0, MAX_ADDRESS)));
  }
}
