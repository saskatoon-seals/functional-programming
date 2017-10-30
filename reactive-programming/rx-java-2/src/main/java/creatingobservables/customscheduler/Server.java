package creatingobservables.customscheduler;

import static creatingobservables.customscheduler.Util.log;

import io.reactivex.Observable;

public class Server {

  public static Observable<String> simple() {
    return Observable.create(subscriber -> {
      log("Subscribed");
      subscriber.onNext("A");
      subscriber.onNext("B");
      subscriber.onComplete();
    });
  }
}
