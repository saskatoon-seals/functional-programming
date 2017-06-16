package common;

public final class DisplayUtil {

  private DisplayUtil() { }

  public static <T> void display(T content) {
    if (content instanceof byte[]) {
      for (byte element : (byte[]) content) {
        System.out.print(element);
      }
    }
    else {
      System.out.print(content.toString());
    }

    System.out.println();
  }
}
