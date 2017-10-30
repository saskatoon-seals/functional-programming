package transforming;

import common.ObserverUtil;
import io.reactivex.Observable;

public class FlatMap {

  public static void mapToObservable() {
    Observable<Integer> flatMapped = Observable
        .just(-1, 0, 1)
        .map(value -> 2 / value)
        .flatMap(
            value -> Observable.just(value),
            error -> Observable.just(0),
            () -> Observable.just(42)
        );

    ObserverUtil.subscribePrint(flatMapped, "flatMap");
  }

  /*
   * Combines items from source observable and the result of a given function (sum).
   * Useful when target/mapped items need the access to the source items (avoids the need of tuples)
   */
  public static void combineSourceAndMappedItems() {
    Observable<Integer> flatMapped = Observable
        .just(5, 432)
        .flatMap(
            value -> Observable.range(value, 2),
            (x, y) -> x + y); //Observable instance will emit the results of this function

   ObserverUtil.subscribePrint(flatMapped, "flatMap");
  }
}
