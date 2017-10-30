package creatingobservables;

import static common.ObserverUtil.subscribePrint;
import static common.ThreadUtil.delayMillis;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class Timers {
  public static void run() {
    //Spins a new daemon thread with Schedulers.computation()
    Observable<Long> interval = Observable.interval(500L, TimeUnit.MILLISECONDS);

    subscribePrint(interval, "Interval observable");

    delayMillis(2000);
  }
}
