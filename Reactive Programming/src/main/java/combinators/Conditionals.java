package combinators;

import static common.ObserverUtil.blockingSubscribePrint;
import static common.ObserverUtil.subscribePrint;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;

public class Conditionals {

  static Observable<Long> interval = Observable.interval(500L, TimeUnit.MILLISECONDS).take(2);

  //Mirrors the emission of a source that starts emitting items first
  @SuppressWarnings("unchecked") //Mixing String and Long data types
  static void amb() {
    //Example 1 (emission from the words source, because interval starts with a delay):
    Observable<String> words = Observable.just("Some", "Other");


    Observable<? extends Object> amb = Observable.ambArray(words, interval);
    blockingSubscribePrint(amb, "Amb 1");

    //Example 2 (emission from a random source):
    Random r = new Random();
    Observable<String> source1 = Observable.just("data from source 1")
        .delay(r.nextInt(1000), TimeUnit.MILLISECONDS);
    Observable<String> source2 = Observable.just("data from source 2")
        .delay(r.nextInt(1000), TimeUnit.MILLISECONDS);

    blockingSubscribePrint(Observable.ambArray(source1, source2), "Amb 2");
  }

  /*
   * Useful for displaying loading animations in GUI applications.
   *
   * E.g.: loadingAnimationObservable.takeUntil(requestObservable);
   */
  static void takeUntilWhile() {
    Observable<String> words =
        Observable.just("one", "way", "or", "another", "I'll", "learn", "RxJava")
                  .zipWith(
                      Observable.interval(200L, TimeUnit.MILLISECONDS),
                      (word, timeIndex) -> word);

    //Emits words only the first second (2 words)
    blockingSubscribePrint(words.takeUntil(interval)
                                .delay(1L, TimeUnit.SECONDS), //Delays notifications for 1 s
                           "takeUntil");

    //Will be emitting until words are longer than 2 letters
    blockingSubscribePrint(words.delay(800L, TimeUnit.MILLISECONDS) //Delay (order isn't important)
                                .takeWhile(word -> word.length() > 2),
                           "takeWhile");

    //Will emit words from 'or' till the end
    //Doesn't emit until the condition is satisfied == first item emitted from interval in 500 ms.
    blockingSubscribePrint(words.skipUntil(interval),
                           "skipUntil");
  }

  static void defaultIfEmpty() {
    //Will emit 5
    Observable<Object> test = Observable.empty()
                                        .defaultIfEmpty(5);

    subscribePrint(test, "defaultIfEmpty");
  }
}
