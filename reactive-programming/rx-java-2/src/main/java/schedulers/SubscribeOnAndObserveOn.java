package schedulers;

import java.util.concurrent.CountDownLatch;

import common.ObserverUtil;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

/*
 * Conclusion: Don't specify schedulers in methods creating Observables (they can't be overrided).
 * Leave this to the callers of these factory methods.
 *
 * SubscribeOn is useful in a case when Observable instance blocks the caller thread when
 * subscribing to that observable.
 *
 * Difference between subscribeOn and observeOn is that subscribeOn allows only a single scheduler
 * while observeOn allows a different scheduler to execute the observable logic on.
 *
 * E.g.: you can read files from the FS with IO scheduler with subscribeOn and then observe the
 * results on the event thread.
 */
public class SubscribeOnAndObserveOn {

  //SubscribeOn:

  //Runs on the main thread
  private static void range() {
    Observable.range(20, 4)
              .doOnEach(ObserverUtil.debug("Source"))
              .subscribe();

    System.out.println("The end!");
  }

  private static void rangeWithScheduler() {
    CountDownLatch latch = new CountDownLatch(1);

    Observable.range(20, 4)
              .doOnEach(ObserverUtil.debug("Source"))
              .subscribeOn(Schedulers.computation())
              .doFinally(() -> latch.countDown())
              .subscribe();

    //This is printed by the main thread somewhere in between items emitted by range
    System.out.println("The end!");

    await(latch);
  }

  //The closest scheduler to the beginning of observable is taken (computation())
  private static void mixedSchedulers() {
    CountDownLatch latch = new CountDownLatch(1);

    Observable<Integer> range =
      Observable.range(20, 3)
                .doOnEach(ObserverUtil.debug("Source"))
                .subscribeOn(Schedulers.computation());

    Observable<Character> chars =
        range.map(n -> n + 48)
             .map(Character::toChars)
             .subscribeOn(Schedulers.io())
             .map(c -> c[0])
             .subscribeOn(Schedulers.newThread())
             .doOnEach(ObserverUtil.debug("Chars ", "    "))
             .doFinally(() -> latch.countDown());

    chars.subscribe();
    System.out.println("The end!");
    await(latch);
  }

  //ObserveOn:

  /*
   * we tell the Observable chain to execution on the main thread after subscribing until it reaches
   * the observeOn().
   *
   * Part of the chain before the observeOn blocks the main thread
   */
  private static void observeOn() {
    CountDownLatch latch = new CountDownLatch(1);

    Observable<Integer> range =
      Observable.range(20, 3)
                .doOnEach(ObserverUtil.debug("Source"));

    Observable<Character> chars =
        range.map(n -> n + 48)
             .doOnEach(ObserverUtil.debug("+48 ", "    ")) //will use main thread
             .map(Character::toChars)
             .map(c -> c[0])
             .observeOn(Schedulers.computation())
             .doOnEach(ObserverUtil.debug("Chars ", "    ")) //will use computation()
             .doFinally(() -> latch.countDown());

    chars.subscribe();

    /*
     * This is printed after all the notifications pass through the ObserveOn() operator.
     * "The end!" is then printed in between items that are emitted on computation thread.
     */
    System.out.println("The end!");
    await(latch);
  }

  //The true parallelism:

  private static void flatMapParallelism() {
    Observable.range(20, 5)
              .flatMap(value -> Observable.range(value, 3)
                                          .subscribeOn(Schedulers.computation())
                                          .doOnEach(ObserverUtil.debug("Source"))
                  )
              .subscribe();
  }

  public static void main(String... args) {
    flatMapParallelism();
  }

  private static void await(CountDownLatch latch) {
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }
}
