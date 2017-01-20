package common;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;

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
