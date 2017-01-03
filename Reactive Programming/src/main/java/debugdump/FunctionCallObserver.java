package debugdump;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class FunctionCallObserver {

  private final CountDownLatch latch;

  public FunctionCallObserver(int numOfObservables) {
    latch = new CountDownLatch(numOfObservables);
  }

  private void subscribe(Observable<String> functionCallObservable, Path output) {
    functionCallObservable
      .subscribeOn(Schedulers.computation())
      .doFinally(() -> latch.countDown())
      .subscribe(
          //on successful execution of command:
          result -> Files.write(output,
                                Arrays.asList(result),
                                StandardOpenOption.CREATE),

          //if command threw an exception:
          exception -> Files.write(output,
                                   Arrays.asList(exception.toString()),
                                   StandardOpenOption.CREATE)
      );
  }

  private Path getOuputFilePath(int index) {
    return Paths.get("src", "main", "resources",
        String.format("%s%d", "EchoCommand", index));
  }

  private void waitForResults() {
    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  private void blockingRun(Observable<String>... observables) {
    IntStream.range(0, observables.length)
             .forEach(index -> subscribe(observables[index],
                                         getOuputFilePath(index)));

    waitForResults();
  }

  public static void main(String... args) {
    int numOfObservables = 2;

    CommandInterface echoCommand =
        (String[] input) ->  Arrays.stream(input)
                                   .collect(Collectors.joining(" "));

    FunctionCallObserver observer = new FunctionCallObserver(numOfObservables);

    Observable<String> observableA = FunctionCallObservable.create(echoCommand, "Ales");
    Observable<String> observableB = FunctionCallObservable.create(echoCommand, "Maja");

    observer.blockingRun(observableA, observableB);
  }
}
