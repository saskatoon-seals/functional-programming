package common;

import java.util.Arrays;
import java.util.concurrent.CountDownLatch;

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

  /**
   * Subscribes to an observable, printing all its emissions.
   * Blocks until the observable calls onCompleted or onError.
   */
  public static <T> void blockingSubscribePrint(Observable<T> observable, String name) {
    CountDownLatch latch = new CountDownLatch(1);

    subscribePrint(
        observable.doFinally(() -> latch.countDown()),
        name
    );

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
