package handlingerrors;

import common.ObserverUtil;
import io.reactivex.Observable;

//Errors terminate the Observable chain of actions
public class HandlingErrors {

  static void run() {
    Observable<Integer> numbers =
        Observable.just("1", "2", "three", "4", "5")
                  .map(Integer::parseInt)
                  .onErrorReturnItem(-1);

    ObserverUtil.subscribePrint(numbers, "numbers with error");
  }

  public static void main(String... args) {
    HandlingErrors.run();
  }
}
