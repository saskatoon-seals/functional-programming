package creatingobservables;

import static common.ObserverUtil.subscribePrint;

import java.util.concurrent.TimeUnit;

import common.ThreadUtil;
import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;

/*
 * HOT OBSERVABLES:
 *
 * Observers are invoked synchronously
 */
public class HotObservables {

  public static void connectableObservable() {
    Observable<Long> interval = Observable.interval(500L, TimeUnit.MILLISECONDS);
    //Creates a hot observable from a cold observable (broadcasting system)
    ConnectableObservable<Long> published = interval.publish();

    Disposable firstSubscription = subscribePrint(published, "First");
    Disposable secondSubscription = subscribePrint(published, "Second");

    //Starts broadcasting (emitting items)
    published.connect();

    ThreadUtil.delayMillis(5000);
      //Doesn't receive items that were emitted before it subscribed (if published used)
    Disposable thirdSubscription = subscribePrint(published, "Third");
    ThreadUtil.delayMillis(5000);

    firstSubscription.dispose();
    secondSubscription.dispose();
    thirdSubscription.dispose();
  }

  public static void refCountObservable() {
    Observable<Long> refCountObservable = Observable.interval(500L, TimeUnit.MILLISECONDS) //cold
                                                    .publish() //hot observable
                                                    .refCount();

    Disposable firstSubscription = subscribePrint(refCountObservable, "First");
    Disposable secondSubscription = subscribePrint(refCountObservable, "Second");

    ThreadUtil.delayMillis(2000);

    firstSubscription.dispose();
    //Deactivation: After the last subscriber unsubscribes, the observable stops emitting (temporarily)
    secondSubscription.dispose();

    //Observable starts emitting from the beginning
    Disposable thirdSubscription = subscribePrint(refCountObservable, "Third");
    ThreadUtil.delayMillis(5000);

    thirdSubscription.dispose();
  }
}
