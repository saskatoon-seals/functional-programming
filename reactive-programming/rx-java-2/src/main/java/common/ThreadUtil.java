package common;

public class ThreadUtil {
  public static void delayMillis(int millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
