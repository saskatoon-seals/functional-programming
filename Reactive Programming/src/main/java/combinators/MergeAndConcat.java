package combinators;

import common.ObserverUtil;
import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;
import reactivesum.functional.FactoryMethods;

/**
 * Merge is subscribed to all the observables at the same time, while Concat is subscribed
 * only to a single observable at time. It waits until that one is finished and then it
 * subscribes to the rest.
 */
public class MergeAndConcat {

  static void merge() {
    Observable<String> merged = Observable.merge(
        CombineLatest.greetings(),
        CombineLatest.names(),
        CombineLatest.punctuation());

    ObserverUtil.blockingSubscribePrint(merged, "Merged");
  }

  //Names observable instance is prepended to greetings observable instatance.
  static void concat() {
    Observable<String> concat = Observable.concat(
        CombineLatest.greetings(),  //It will emit greetings every second
        CombineLatest.names(),      //Then it will emit names every 1.5 seconds
        CombineLatest.punctuation() //At the end it will start emitting punctuations every 1.1 s.
      );

    ObserverUtil.blockingSubscribePrint(concat, "Concat");
  }

  //Reverses the order of concatenation compared to concat()
  static void startWith() {
    Observable<String> concat =
        CombineLatest.greetings()
                     .startWith(CombineLatest.names())
                     .startWith(CombineLatest.punctuation());

    ObserverUtil.blockingSubscribePrint(concat, "Start With");
  }

  static void reactiveSum() {
    ConnectableObservable<String> input = FactoryMethods.from(System.in);
    Observable<Double> a = FactoryMethods.varStream("a", input);
    Observable<Double> b = FactoryMethods.varStream("b", input);

    Observable<Double> sum = Observable.combineLatest(a.startWith(0.0),
                                                      b.startWith(0.0),
                                                      (x, y) -> x + y);

    ObserverUtil.subscribePrint(sum, "Sum");

    input.connect();
  }
}
