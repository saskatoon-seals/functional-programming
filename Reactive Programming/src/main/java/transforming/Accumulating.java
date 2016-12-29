package transforming;

import static common.ObserverUtil.subscribePrint;

import java.nio.file.Paths;

import io.reactivex.Observable;

/*
 * The function passed to the scan() method is called an accumulator.
 */
public class Accumulating {

  static void accumulateNumbers() {
    Observable<Integer> scan = Observable.range(1, 10)
                                         .scan((sum, currentValue) -> sum + currentValue);

    subscribePrint(scan, "Sum");
    subscribePrint(scan.last(0).toObservable(), "Last Sum");
  }

  static void countNumberOfLinesInFile() {
    Observable<String> file = FileStreamOperations.from(
        Paths.get("src", "main", "resources", "loremipsum.txt")
      );

    Observable<Integer> scan = file.scan(0, (numOfLines, line) -> numOfLines + 1);

    subscribePrint(scan.last(0).toObservable(), "wc -l");
  }

  public static void main(String... args) {
    Accumulating.countNumberOfLinesInFile();
  }
}
