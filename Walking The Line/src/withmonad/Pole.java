package withmonad;

public class Pole {
  private static final int MAX_DIFFERENCE = 3;
  private final int left;
  private final int right;

  private Pole(int left, int right) {
    this.left = left;
    this.right = right;
  }

  //************************************************************************************************
  //                                      APIs
  //************************************************************************************************

  public static Pole create(int left, int right) {
    return new Pole(left, right);
  }

  public Pole landLeft(int num) {
    return land(left + num, right);
  }

  public Pole landRight(int num) {
    return land(left, right + num);
  }

  @Override
  public String toString() {
    return String.format("left: %d, right: %d", left, right);
  }

  //************************************************************************************************
  //                                    Helper methods
  //************************************************************************************************

  private Pole land(int numLeft, int numRight) {
    if (canLand(numLeft, numRight)) {
      return Pole.create(
          numLeft,
          numRight
      );
    }

    throw new IllegalStateException();
  }

  private static boolean canLand(int numLeft, int numRight) {
    return Math.abs(numLeft - numRight) <= MAX_DIFFERENCE;
  }
}
