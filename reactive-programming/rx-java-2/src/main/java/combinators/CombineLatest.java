package combinators;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class CombineLatest {

  private static <T, U> T getFirstArg(T first, U second) {
    return first;
  }

  static Observable<String> greetings() {
    return Observable.just("Hello", "Hi", "Howdy", "Zdravei", "Yo", "Good to see ya")
                     .zipWith(
                         Observable.interval(1, TimeUnit.SECONDS),
                         CombineLatest::getFirstArg
                     );
  }

  static Observable<String> names() {
    return Observable.just("Meddle", "Tanya", "Ales", "Maja")
                     .zipWith(
                         Observable.interval(1500L, TimeUnit.MILLISECONDS),
                         CombineLatest::getFirstArg
                     );
  }

  static Observable<String> punctuation() {
    return Observable.just(".", "?", "!", ";")
                     .zipWith(
                         Observable.interval(1100L, TimeUnit.MILLISECONDS),
                         CombineLatest::getFirstArg
                     );
  }

  static Observable<String> combine() {
    return Observable.combineLatest(
        punctuation(), names(), greetings(),
        (punctuation, name, greeting) -> String.format("%s %s%s", greeting, name, punctuation)
      );
  }
}
