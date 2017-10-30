package creatingobservables;

import static common.ObserverUtil.subscribePrint;
import static common.ThreadUtil.delayMillis;

import java.util.concurrent.TimeUnit;

import common.ObserverUtil;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

/*
 * Subject acts as both observable (it emits items) and observer instances
 * (it can subscribe to observable).
 */
public class SubjectFactory {

  public static void hotInterval() {
    Observable<Long> interval = Observable.interval(100L, TimeUnit.MILLISECONDS);

    /*
     * TOURNS INTO HOT OBSERVABLE SUBJECT:
     *
     * The new subject instance is subscribed to the interval Observable instance. Subject will be
     * emitting the same items the source is emitting. Subject is a hot observable.
     */
    Subject<Long> publishSubject = PublishSubject.create();

    //Acts as observer because it subscribes to observable
    interval.subscribe(publishSubject);

    Disposable subscription1 = subscribePrint(publishSubject, "First");
    Disposable subscription2 = subscribePrint(publishSubject, "Second");

    delayMillis(333);
    //Subject emits costum notification
    publishSubject.onNext(555L);
    Disposable subscription3 = subscribePrint(publishSubject, "Third");
    delayMillis(333);

    ObserverUtil.unsubscribe(subscription1, subscription2, subscription3);
  }

  /*
   * BEHAVIOR SUBJECT:
   *
   * When observer subscribes to it, it emits the item most recently emitted by the source
   * Observable instance and then continues to emit any other items emitted later by the source.
   */
  public static class ReactiveSum {
    private BehaviorSubject<Double> a = BehaviorSubject.createDefault(0.0);
    private BehaviorSubject<Double> b = BehaviorSubject.createDefault(0.0);
    private BehaviorSubject<Double> c = BehaviorSubject.createDefault(0.0);

    public ReactiveSum() {
      Observable.combineLatest(a, b, (x, y) -> x + y)
                .subscribe(c);
    }

    public double getA() {
      return a.getValue();
    }

    public void setA(double a) {
      this.a.onNext(a);
    }

    public double getB() {
      return b.getValue();
    }

    public void setB(double b) {
      this.b.onNext(b);
    }

    public double getC() {
      return c.getValue();
    }

    //Never return subject, because it gives too much control to the client (access to onNext)
    public Observable<Double> getObservableC() {
      return c.hide();
    }
  }
}
