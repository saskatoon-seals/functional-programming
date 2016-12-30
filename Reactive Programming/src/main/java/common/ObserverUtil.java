package common;

import java.util.Arrays;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class ObserverUtil {

  /**
   * Unsubscribes given subscriptions
   *
   * @param subscriptions - subscriptions to unsubscribe
   */
  public static void unsubscribe(Disposable... subscriptions) {
    Arrays.stream(subscriptions)
          .forEach(Disposable::dispose);
  }

  /**
   * Creates a subscription to an observable source.
   *
   * @param observable - observable source
   * @param name - name of subscription
   * @return - disposable subscription
   */
  public static <T> Disposable subscribePrint(Observable<T> observable, String name) {
    return observable.subscribe(
        value -> System.out.println(name + " : "  + value),
        exception -> {
          System.out.println(name);
          exception.printStackTrace();
          },
        () -> System.out.println(name + " ended!")
      );
  }
}
