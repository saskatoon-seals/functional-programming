package common;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.disposables.Disposable;
import io.reactivex.observables.ConnectableObservable;

public class CreateObservable {

  //************************************************************************************************
  //                                        APIs
  //************************************************************************************************

  public static Observable<Path> listFolder(Path dir, String glob) {
    return Observable.create(subscriber -> {
      try {
        DirectoryStream<Path> stream =
            Files.newDirectoryStream(dir, glob);

        subscriber.setDisposable(
            new CloseResource(stream)
        );

        Observable.fromIterable(stream)
                  .subscribe(subscriber::onNext);

      } catch (DirectoryIteratorException ex) {
        subscriber.onError(ex);
      } catch (IOException ioe) {
        subscriber.onError(ioe);
      }
    });
}

  public static Observable<String> from(final Path path) {
    return Observable.create(subscriber -> {
      try {
        BufferedReader reader = Files.newBufferedReader(path);

        subscriber.setDisposable(
            new CloseResource(reader)
        );

        readFileContents(reader, subscriber);

      } catch (IOException ioe) {
        if (!subscriber.isDisposed()) {
          subscriber.onError(ioe);
        }
      }
    });
  }

  @SafeVarargs
  public static <T> Observable<T> sorted(Comparator<? super T> comparator, T... data) {
    List<T> listData = Arrays.asList(data);
    listData.sort(comparator);

    return Observable.fromIterable(listData);
  }

  public static Observable<String> from(final Reader reader) {
    return Observable.defer(() -> {
      return from(new BufferedReader(reader)).refCount();
    }).cache();
  }

  public static ConnectableObservable<String> from(final BufferedReader reader) {
    return Observable.<String>create(subscriber -> {
      try {
        String line;

        if (subscriber.isDisposed())
          return;

        while (!subscriber.isDisposed() && (line = reader.readLine()) != null) {
          if (line.equals("exit")) {
            break;
          }

          subscriber.onNext(line);
        }
      } catch (IOException e) {
        subscriber.onError(e);
      }

      if (!subscriber.isDisposed()) {
        subscriber.onComplete();
      }
    }).publish();
  }

  public static Observable<Long> interval(List<Long> gaps, TimeUnit unit, Scheduler scheduler) {
    if (gaps == null || gaps.isEmpty()) {
      throw new IllegalArgumentException("Provide one or more interval gaps!");
    }

    return Observable.<Long>create(subscriber -> {
      int size = gaps.size();

      Worker worker = scheduler.createWorker();
      subscriber.setDisposable(worker);

      final Runnable action = new Runnable() {
        long current = 0;

        @Override
        public void run() {
          subscriber.onNext(current++);

          long currentGap = gaps.get((int) current % size);
          worker.schedule(this, currentGap, unit);
        }
      };

      worker.schedule(action, gaps.get(0), unit);
    });
  }



  //************************************************************************************************
  //                                    Helper methods
  //************************************************************************************************

  private static void readFileContents(BufferedReader reader,
      ObservableEmitter<String> subscriber) throws IOException {
    String line = null;

    while ((line = reader.readLine()) != null && !subscriber.isDisposed()) {
      subscriber.onNext(line);
    }

    if (!subscriber.isDisposed()) {
      subscriber.onComplete();
    }
  }

  //************************************************************************************************
  //                                    Helper classes
  //************************************************************************************************

  private static class CloseResource implements Disposable {
    private Closeable resource;

    private CloseResource(Closeable resource) {
      this.resource = resource;
    }

    @Override
    public void dispose() {
      try {
        resource.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Override
    public boolean isDisposed() {
      return false;
    }

  }

  public static void main(String... args) {
    ObserverUtil.subscribePrint(
        CreateObservable.from(Paths.get("src", "main", "resources", "loremipsum.txt")),
        "File");
  }
}
