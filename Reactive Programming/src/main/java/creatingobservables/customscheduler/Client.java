package creatingobservables.customscheduler;

import static creatingobservables.customscheduler.Server.simple;
import static creatingobservables.customscheduler.Util.log;

import io.reactivex.Observable;

//Retrofitting existing APIs:
public class Client {

  public static void main(String... args) {
    log("Starting");
    final Observable<String> obs = simple();
    log("Created");

    obs.subscribeOn(Util.schedulerA)
       .subscribe(x -> log("Got " + x),
                  Throwable::printStackTrace,
                  () -> log("Completed"));

    //Client (main) thread exits before subscription ends.
    log("Exiting");
  }
}
