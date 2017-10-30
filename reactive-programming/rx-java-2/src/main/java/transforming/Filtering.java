package transforming;

import static common.ObserverUtil.subscribePrint;

import java.util.Arrays;

import io.reactivex.Observable;

public class Filtering {

  private static Observable<Integer> numbers = Observable.just(1, 13, 32, 45, 21, 8, 98, 103, 55);

  static void filterNumbers() {
    subscribePrint(numbers.filter(n -> n % 2 == 0),
                   "Filter");

    subscribePrint(numbers.takeLast(3),"Take last");
  }

  static void filterWords() {
    Observable<String> words = Observable.just("One", "of", "the", "few",
        "of", "the", "crew", "crew");
    subscribePrint(words.distinct(), "Distinct");
    subscribePrint(words.distinctUntilChanged(), "Distinct until changed");

    Observable<?> various = Observable.fromIterable(Arrays.asList("1", 2, 3.0, 4, 5L));
    subscribePrint(various, "Various");
  }

  static void lastOrDefault() {
    subscribePrint(numbers.last(200)
                          .toObservable(), "Last or Default I");

    subscribePrint(Observable.empty()
                             .last(200)
                             .toObservable(), "Last or Default II");

    subscribePrint(numbers.empty(), "Empty");
  }

  static void firstOrDefault() {
    subscribePrint(numbers.firstOrError()
                          .toObservable(), "First");

    //Exception is not thrown in toObservable, but rather inside observable.subscribe(..->..) method.
//    subscribePrint(Observable.empty()
//                             .firstOrError()
//                             .toObservable(), "First");
  }
}
