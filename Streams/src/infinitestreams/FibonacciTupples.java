package infinitestreams;

import java.util.Arrays;
import java.util.stream.Stream;

public class FibonacciTupples {

  public static int[][] generate(int number) {
    int[] seed = {0, 1};
    
    return Stream.iterate(seed, value -> new int[] {value[1], value[0] + value[1]})
                 .limit(number)
                 .toArray(int[][]::new);    
  }
  
  public static void print(int[][] values) {
    Arrays.stream(values)
          .forEach(value -> System.out.println(
              String.format("(%d, %d)", value[0], value[1])));
  }
  
  public static void main(String... args) {
    print(generate(10));
  }
}
