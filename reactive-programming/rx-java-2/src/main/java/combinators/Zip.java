package combinators;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class Zip {

  static Observable<Integer> zipNumbers() {
    return Observable.zip(
        Observable.just(1, 3, 4),
        Observable.just(5,  2,  6),
        (x, y) -> x + y
      );
  }

  static Observable<String> timedZip() {
    return Observable.zip(
        Observable.just("Z", "I", "P", "P"),
        Observable.interval(1, TimeUnit.SECONDS),
        (letter, timeIndex) -> letter
      );
  }

  static Observable<String> instanceTimedZip() {
    return Observable.just("Z", "I", "P", "P")
                     .zipWith(Observable.interval(300L, TimeUnit.MILLISECONDS),
                              (letter, timeIndex) -> letter);
  }
}
