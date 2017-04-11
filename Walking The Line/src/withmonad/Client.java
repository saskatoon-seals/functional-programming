package withmonad;

public class Client {

  private static void landingWithWrapper(Pole startPole) {
    System.out.println(
      PoleWrapper.create(startPole)
                 .landLeft(3)
                 .landRight(1)
                 .landRight(6)
    );
  }

  private static void rawLanding(Pole startPole) {
    try {
      System.out.println(
        startPole.landLeft(3)
                 .landRight(1)
                 .landRight(6)
      );
    } catch (Exception e) {

    }
  }

  public static void main(String... args) {
    landingWithWrapper(
        Pole.create(0, 0)
    );

    rawLanding(
        Pole.create(0, 0)
    );
  }
}
