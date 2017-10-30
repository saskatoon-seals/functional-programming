package observables;

import java.io.StringReader;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import common.CreateObservable;
import io.reactivex.Observable;

public class BlockingObservablesAndOperators implements Runnable {
  @Override
  public void run() {
    Observable
      .interval(100L, TimeUnit.MILLISECONDS)
      .take(5)
      .blockingSubscribe(System.out::println);
    System.out.println("END");

    Integer first = Observable.range(3, 13).blockingFirst();
    System.out.println(first);
    Integer last = Observable.range(3, 13).blockingLast();
    System.out.println(last);

    Iterable<Long> next = Observable
        .interval(100L, TimeUnit.MILLISECONDS)
        .blockingNext();
    Iterator<Long> iterator = next.iterator();
    System.out.println(iterator.next());
    System.out.println(iterator.next());
    System.out.println(iterator.next());

    Iterable<Long> latest = Observable
        .interval(1000L, TimeUnit.MILLISECONDS)
        .blockingLatest();
    iterator = latest.iterator();
    System.out.println(iterator.next());

    try {
      Thread.sleep(5500L);
    } catch (InterruptedException e) {

    }
    System.out.println(iterator.next());
    System.out.println(iterator.next());

    //Will contain all the items emitted by the source observable
    List<Integer> single = Observable
        .range(5, 15)
        .toList()
        .blockingGet();
    System.out.println(single);

    Observable.range(10, 100).count().subscribe(System.out::println);

    StringReader reader = new StringReader("One\nTwo\nThree");
    Observable<String> observable = CreateObservable.from(reader);

    System.out.println(observable.count().blockingGet());
    System.out.println(observable.toList().blockingGet());
  }

  public static void main(String[] args) {
    new BlockingObservablesAndOperators().run();
  }

}