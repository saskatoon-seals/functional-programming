package transforming;

import static common.ObserverUtil.subscribePrint;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Timed;

public class UsingGroupBy {
  private static final List<String> ALBUMS = Arrays.asList(
      "The Piper at the Gates of Dawn",
      "A Saucerful of Secrets",
      "More",
      "Ummagumma",
      "Atom Heart Mother",
      "Meddle",
      "Obscured by Clouds",
      "The Dark Side of the Moon",
      "Wish You Were Here",
      "Animals",
      "The Wall");

  /**
   * Splits items into groups, where each group becomes a group observable that contains one or
   * more items.
   */
  public static void groupByNumOfWordsInAlbum() {
    Observable.fromIterable(ALBUMS)
              .groupBy(album -> album.split(" ").length)
              .subscribe(groupObservable ->
                subscribePrint(groupObservable, groupObservable.getKey() + " words"));
  }

  public static void groupByAndTransform() {
    Observable.fromIterable(ALBUMS)
              .groupBy(
                  album -> album.replaceAll("[^mM]", "").length(), //group by num of m's (key selector)
                  album -> album.replaceAll("[mM]", "*") //transform (value selector)
              )
              .subscribe(obs -> subscribePrint(obs, obs.getKey() + " occurances of 'm'"));
  }

  /**
   * Converts every single item emitted by the source observable into a type defined in cast(..)
   */
  public static void variousTransformations() {
    List<Number> list = Arrays.asList(1, 1, 2, 3);
    Observable<Integer> iObs = Observable.fromIterable(list)
                                         .cast(Integer.class);

    subscribePrint(iObs, "Integers");

    Observable<Timed<Number>> timestamp = Observable.fromIterable(list)
                                                    .timestamp();

    subscribePrint(timestamp, "Timestamps");

    Observable<Timed<Long>> timeInterval = Observable.interval(150L, TimeUnit.MILLISECONDS)
                                                     .timeInterval();

    Disposable subscription = subscribePrint(timeInterval, "Time intervals");

    try {
      Thread.sleep(1000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    subscription.dispose();
  }
}
