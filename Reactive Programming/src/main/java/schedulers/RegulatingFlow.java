package schedulers;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import common.CreateObservable;
import common.ObserverUtil;
import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class RegulatingFlow {
  static Observable<String> createFileObservable(Path directoryPath) {
    return CreateObservable
        .listFolder(directoryPath, "*")
        .flatMap(filePath -> {

          if (!Files.isDirectory(filePath)) {
            return CreateObservable.from(filePath)
                                   .subscribeOn(Schedulers.io());
          }

          return Observable.empty();
        });
  }

  public static void main(String... args) {
    ObserverUtil.blockingSubscribePrint(
        createFileObservable(Paths.get("src", "main", "resources")),
        "Too many lines"
    );
  }
}
