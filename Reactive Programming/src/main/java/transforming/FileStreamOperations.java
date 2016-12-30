package transforming;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import common.ObserverUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposables;

public class FileStreamOperations {

  /**
   * Creates observables from all files within 3 given directories. Each file will be an observable.
   * And for every file the content is read line by line (File content is emitted as lines).
   *
   * 1. Lists files within a given directory (path)
   * 2. Lists all the lines for files within a given directory
   */
  public static void run() {
    Observable<String> fileSystemObservable = listFolder(
          Paths.get("/home/ales/Repositories/functional-programming/Reactive Programming/src/main/resources"),
          "{loremipsum.txt}"
        )
        .flatMap(filePath -> from(filePath)); //creates an observable instance from a file

    ObserverUtil.subscribePrint(fileSystemObservable, "File System");
  }

  /**
   * Creates observable instance representing given directory [Factory Method]
   *
   * <p>Observable instance emits all the files in the folder, complying the glob expression
   * as Path objects.
   *
   * @param dir - directory path
   * @param glob - glob expression
   * @return path observable
   */
  private static Observable<Path> listFolder(Path dir, String glob) {
    return Observable.create((ObservableEmitter<Path> subscriber) -> {
      try {
        DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir, glob);

        /*
         * Creates and adds new subscription (the action will execute when subscription is
         * unsubscribed. This is similar to putting the closing of the stream in final block.
         *
         * When subscriber.onComplete() is called, the method within setDisposable() will execute.
         */
        subscriber.setDisposable( //sets disposable on the emitter (subscriber)
            Disposables.fromRunnable( //creates disposable == subscription
                closeResource(dirStream)));

        dirStream.forEach(filePath -> subscriber.onNext(filePath));

        //All the files within a directory are emitted after this point..

        if (!subscriber.isDisposed()) {
          subscriber.onComplete();
        }

      /*
       * onError():
       *
       * If exception occurs, it should be handled and emitted by the resulting Observable instance.
       */
      } catch (DirectoryIteratorException ex) {
        if (!subscriber.isDisposed()) {
          subscriber.onError(ex);
        }
      } catch (IOException ioe) {
        if (!subscriber.isDisposed()) {
          subscriber.onError(ioe);
        }
      }
    });
  }

  /**
   * Creates observable for a given path
   *
   * Reads a file line by line and emits the lines as onNext() notifications.
   *
   * @param path - file path
   * @return string observable
   */
  private static Observable<String> from(final Path path) {
    return Observable.<String>create(subscriber -> {
      try {
        BufferedReader reader = Files.newBufferedReader(path);

        subscriber.setDisposable(
            Disposables.fromRunnable(
                closeResource(reader)));

        String line = null;
        while ((line = reader.readLine()) != null && !subscriber.isDisposed()) {
          subscriber.onNext(line);
        }

        //All the lines within a file have been emitted after this point..

        if (!subscriber.isDisposed()) {
          subscriber.onComplete();
        }
      } catch (IOException ioe) {
        if (!subscriber.isDisposed()) {
          subscriber.onError(ioe);
        }
      }
    });
  }

  //Main:

  public static void main(String... args) {
//    FileStreamOperations.run();
//    FlatMap.mapToObservable();
    FlatMap.combineSourceAndMappedItems();
  }

  //Helper methods:

  private static Runnable closeResource(Closeable resource) {
    return () -> {
      try {
        resource.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    };
  }
}
