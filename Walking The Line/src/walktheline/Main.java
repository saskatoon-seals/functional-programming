package walktheline;

import java.util.Arrays;

/*
 * All of the methods are referentially transparent
 */
public class Main {
  public static final int MAX_DIFFERENCE = 3;
  public static final String FAILED_MSG = "Fell of the rope";
  public static final int LAND_LEFT = 0;
  public static final int LAND_RIGHT = 1;

  public static String walk(int[] startAllignment, int[]... landings) {
    int numLeft = startAllignment[0];
    int numRight = startAllignment[1];

    if (!isBalanced(numLeft, numRight)) {
      return FAILED_MSG;
    }

    return startLanding(
        Arrays.copyOf(startAllignment, startAllignment.length),
        landings
    );
  }

  private static String startLanding(int[] position, int[]... landings) {
    for (int[] landing : landings) {
      int numLeft = position[0];
      int numRight = position[1];
      int numToLand = landing[1];

      if (landLeft(landing) && isBalanced(numLeft + numToLand, numRight)) {
        position[0] = numLeft + numToLand;
      } else if (landLeft(landing)) {
        return FAILED_MSG;
      } else if (landRight(landing) && isBalanced(numLeft, numRight + numToLand)) {
        position[1] = numRight + numToLand;
      } else {
        return FAILED_MSG;
      }
    }

    return String.format(
        "left: %d, right: %d",
        position[0],
        position[1]
    );
  }

  private static boolean landLeft(int[] step) {
    return step[0] == LAND_LEFT;
  }

  private static boolean landRight(int[] step) {
    return step[0] == LAND_RIGHT;
  }

  private static boolean isBalanced(int numLeft, int numRight) {
    return Math.abs(numLeft - numRight) <= MAX_DIFFERENCE;
  }
}
