package schedulers;

import java.util.concurrent.TimeUnit;

import common.ObserverUtil;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class Interval {

  static void interval() {
    Observable.interval(500L, TimeUnit.MILLISECONDS)
              .take(5)
              .doOnEach(ObserverUtil.debug("Default interval"))
              .blockingSubscribe();
  }

  static void intervalWithScheduler() {
    //Runs on the main thread
    Observable.interval(500L, TimeUnit.MILLISECONDS, Schedulers.trampoline())
              .take(5)
              .doOnEach(ObserverUtil.debug("Default interval"))
              .subscribe();
  }

  public static void main(String... args) {
    intervalWithScheduler();
  }
}
