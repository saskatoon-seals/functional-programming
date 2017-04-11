package withmonad;

import java.util.concurrent.Callable;

public class PoleWrapper {
  private boolean hasFallen = true;
  private Pole pole;

  private PoleWrapper() {
    this(null, false);
  }

  private PoleWrapper(Pole pole, boolean hasFallen) {
    this.pole = pole;
    this.hasFallen = hasFallen;
  }

  //************************************************************************************************
  //                                      APIs
  //************************************************************************************************

  public PoleWrapper landLeft(int num) {
    return land(
        () -> pole.landLeft(num)
    );
  }

  public PoleWrapper landRight(int num) {
    return land(
        () -> pole.landRight(num)
    );
  }

  public static PoleWrapper create(Pole pole) {
    if (pole == null) {
      return new PoleWrapper(null, false);
    }

    return new PoleWrapper(pole, true);
  }

  @Override
  public String toString() {
    if (!hasFallen) {
      return "Nothing";
    }

    return pole.toString();
  }

  //************************************************************************************************
  //                                      Helper methods
  //************************************************************************************************

  private PoleWrapper land(Callable<Pole> createPole) {
    if (!hasFallen) {
      return this;
    }

    try {
      return PoleWrapper.create(
          createPole.call()
      );
    } catch (Exception e) {
      System.out.println("Pole creation failed");

      return new PoleWrapper();
    }
  }
}
