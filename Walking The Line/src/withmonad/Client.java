package withmonad;

public class Client {

  /*
   * Benefits of using a monad:
   *
   *  1. Simple client access
   *  2. No try/catch needed (which is not even forced by compiler because it's a runtime exception)
   *  3. No need to worry about alternative value ("Nothing")
   *  4. Requests after the failure are simply ignored (without client worrying about it)
   */
  private static void landingWithWrapper(Pole startPole) {
    System.out.println(
      PoleWrapper.create(startPole)
                 .landLeft(3)
                 .landRight(1)
                 .landRight(6)
    );
  }

  /*
   * Benefits of directly accessing Pole:
   *
   *  1. No need to write a monad :)
   *  2. No need to wrap an object into a monad
   */
  private static void rawLanding(Pole startPole) {
    try {
      System.out.println(
        startPole.landLeft(3)
                 .landRight(1)
                 .landRight(6)
      );
    } catch (Exception e) {
      System.out.println("Nothing");
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
