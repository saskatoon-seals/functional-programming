package creatingobservables;

import static common.ObserverUtil.subscribePrint;
import static common.ThreadUtil.delayMillis;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/*
 * When Observable.subscribe() is called, onSubscribe() callback method is invoked.
 * This method wraps the lambda expression passed into create() method. It receives subscriber
 * as an argument.
 * By default, this happens in the same thread and is blocking, so whatever you do inside create()
 * will block subscribe().
 *
 * Simplified, lambdas inside create() will block subscribe(). Observable blocks the observer.
 */
public class ReadFileAsync {

  public static void run() {
    Path path = Paths.get("/home/ales/Repositories/functional-programming/"
        + "Reactive Programming/src/main/resources/loremipsum.txt");

    List<String> data = null;
    try {
      data = Files.readAllLines(path);
    } catch (IOException e) {
      e.printStackTrace();
    }

    //Schedulers decouple tasks and their execution (running them in a separate thread)
    //Asynchronous subscription (multi-threaded) [trampoline invokes a task within a client thread]
    Observable<String> observable = Factories.fromIterable(data)
                                             .subscribeOn(Schedulers.io()); //Spins a daemon thread.

    //If scheduler isn't used, this method (subscribe) will block.
    Disposable subscription = subscribePrint(observable, "File");

    //Some time is needed between subscription and unsubscribing. In that time events (text data)
    //are transmitted and printed.
    delayMillis(2);

    System.out.println("Before subscription");
    subscription.dispose();
    System.out.println("After subscription");
  }
}
