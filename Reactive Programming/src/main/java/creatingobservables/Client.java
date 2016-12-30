package creatingobservables;

import static common.ObserverUtil.subscribePrint;

import creatingobservables.SubjectFactory.ReactiveSum;

public class Client {

  static void runReactiveSum() {
    ReactiveSum reactiveSum = new ReactiveSum();
    subscribePrint(reactiveSum.getObservableC(), "Reactive Sum");

    reactiveSum.setA(2.0);
    reactiveSum.setB(4.55);
  }

  public static void main(String... args) {
    Timers.run();
  }
}
