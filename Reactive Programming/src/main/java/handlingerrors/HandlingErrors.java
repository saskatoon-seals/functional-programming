package handlingerrors;

import java.util.concurrent.TimeUnit;

import common.ObserverUtil;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class HandlingErrors {

  static void errorReturned() {
    Observable<Integer> numbers =
        Observable.just("1", "two", "3")
                  .map(Integer::parseInt)
                  .onErrorReturn(exception -> -1);

    ObserverUtil.subscribePrint(numbers, "Error returned");
  }

  //Replaces source Observable instance for all Subscriber methods
  static void exceptionResumed() {
    Observable<Integer> defaultOnError = Observable.just(1,  2,  3,  4,  5);

    Observable<Integer> numbers =
        Observable.just("1", "two", "3")
                  .map(Integer::parseInt)
                  .onExceptionResumeNext(defaultOnError);

    ObserverUtil.subscribePrint(numbers, "Exception resumed");
  }

  static void errorResumed() {
    Observable<Integer> defaultOnError = Observable.just(1,  2,  3,  4,  5);

    Observable<Integer> numbers =
        Observable.just("1", "two", "3")
                  .doOnNext(number -> {
                    assert !number.equals("three");
                  })
                  .map(Integer::parseInt)
                  .onErrorResumeNext(error -> defaultOnError); //Replaces observable source

    ObserverUtil.subscribePrint(numbers, "Error resumed");
  }

  /*
   * Retrying technique:
   *
   * If error occures, subscribers will resubscribe to the observable and try all from the start
   */

  static void retry() {
    ObserverUtil.subscribePrint(Observable.create(new ErrorEmitter())
                                          .retry(),
                                "Retry");
  }

  /*
   * The following happens:
   *
   *  1. Single delayed retry
   *  2. 3 retries from retry(predicate)
   *  3. On 5th try on error notification will be emitted
   */
  static void retryWithDelay() {
    Observable<Integer> when =
        Observable.create(new ErrorEmitter())
                  .retryWhen(HandlingErrors::getDelay)
                  .retry(HandlingErrors::shouldResubsribe);

    ObserverUtil.blockingSubscribePrint(when, "Retry with delay.");
  }

  //************************************************************************************************
  //                                  Helper methods
  //************************************************************************************************

  private static Observable<Long> getDelay(Observable<Throwable> errorObservable) {
    return errorObservable.flatMap(HandlingErrors::errorHandler);
  }

  private static Observable<Long> errorHandler(Throwable error) {
    if (error instanceof FooException) {
      //Happens on first execution
      System.out.println("Delaying...");
      return Observable.timer(1L,  TimeUnit.SECONDS);
    }

    //Happens on 2nd, 3rd and 4th execution
    return Observable.error(error);
  }

  //Happens on 2nd, 3rd and 4th execution
  private static Boolean shouldResubsribe(int attempts, Throwable error) {
    return (error instanceof BooException) && attempts < 3;
  }

  //************************************************************************************************
  //                                  Inner classes
  //************************************************************************************************

  static class FooException extends RuntimeException {
    public FooException() {
      super("Foo!");
    }
  }

  static class BooException extends RuntimeException {
    public BooException() {
      super("Boo!");
    }
  }

  //This class can be passed to Observable.create()
  static class ErrorEmitter implements ObservableOnSubscribe<Integer> {
    private int throwAnErrorCounter = 5;

    @Override
    public void subscribe(ObservableEmitter<Integer> subscriber) {
      subscriber.onNext(1);
      subscriber.onNext(2);

      if (throwAnErrorCounter > 4) {
        throwAnErrorCounter--;
        subscriber.onError(new FooException());
        return;
      }
      if (throwAnErrorCounter > 0) {
        throwAnErrorCounter--;
        subscriber.onError(new BooException());
        return;
      }

      subscriber.onNext(3);
      subscriber.onNext(4);
      subscriber.onComplete();
    }
  }
}
