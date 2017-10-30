package rxjavalegacy;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import rx.Observable;
import rx.subscriptions.Subscriptions;

public class CreatingObservables {

  public static Observable<String> from(final Path path) {
    return Observable.<String>create(subscriber -> {
      try {
        BufferedReader reader = Files.newBufferedReader(path);
        subscriber.add(Subscriptions.create(() -> {
          try {
            reader.close();
          } catch (IOException e) {
            e.printStackTrace();
          }
        }));

        String line = null;
        while ((line = reader.readLine()) != null && !subscriber.isUnsubscribed()) {
          subscriber.onNext(line);
        }
        if (!subscriber.isUnsubscribed()) {
          subscriber.onCompleted();
        }
      } catch (IOException ioe) {
        if (!subscriber.isUnsubscribed()) {
          subscriber.onError(ioe);
        }
      }
    });
}
}
