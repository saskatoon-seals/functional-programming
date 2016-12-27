package creatingobservables;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

/**
 * What's the difference between Observable.just and Observable.from:
 *
 *   1. just:  Converts two items into an ObservableSource that emits those items.
 *   2. from: Converts an {@link Iterable} sequence into an ObservableSource that emits the items
 *            in the sequence.
 *
 * Sequential vs. random order of emitting items. From is for collections, just is for single items.
 */
public class Factories {

  public static void arrayObservable() {
    List<String> colors = Arrays.asList("blue", "yellow", "green");

    Observable<String> colorsObservable = Observable.fromIterable(colors);

    colorsObservable.subscribe(color -> System.out.println("Color: " + color),
                               e -> e.printStackTrace(),
                               () -> System.out.println("Done!"));
  }

  public static void directoryObservable() {
    Path resources = Paths.get("/home/ales/Repositories/functional-programming");

    try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(resources)) {
      Observable<Path> dirObservable = Observable.fromIterable(dirStream);
      dirObservable.subscribe(System.out::println);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void objectObservable() {
    Observable.just(new User("Ales", "Ursic"), new User("Maja", "Primozic"))
              .map(user -> String.format("%s %s", user.getFirstName(), user.getLastName()))
              .subscribe(System.out::println);
  }

  /**
   * Factory method for creating an observable from an iterable source.
   *
   * <p>NOTE: Logic passed to create() method is triggered when the Observable.subscribe() method is
   * invoked on the Observable instance.
   * It's also checking if subscriber is still subscribed to the observable before sending every
   * message.
   *
   * @param iterable - source for creating an observable (even generator)
   * @return observable
   */
  public static <T> Observable<T> fromIterable(final Iterable<T> iterable) {
    return Observable.create((ObservableEmitter<T> subscriber) -> {
      try {
        if (subscriber.isDisposed())
          return;
        iterable.forEach(subscriber::onNext);

        if (!subscriber.isDisposed()) {
          subscriber.onComplete();
        }
      } catch (Exception e) {
        if (!subscriber.isDisposed()) {
          subscriber.onError(e);
        }
      }
    });
  }
}
