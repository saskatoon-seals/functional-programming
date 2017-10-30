package debugdump;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class FunctionCallObservable {

  /**
   * Creates observable for the given command
   *
   * @param command - command to execute
   * @return observable
   */
  public static Observable<String> create(CommandInterface command, String... args) {
    return Observable.create(subscriber -> {
        if (subscriber.isDisposed())
          return;

        //Executes command and publishes result
        publishResult(subscriber, command, args);

        if (!subscriber.isDisposed()) {
          subscriber.onComplete();
        }
    });
  }

  //************************************************************************************************
  //                                    Helper methods
  //************************************************************************************************

  /**
   * Notifies the subscriber of the result of the command's execution
   *
   * @param subscriber - subscriber
   * @param command - command to execute
   * @param args - command arguments
   */
  private static void publishResult(
      ObservableEmitter<String> subscriber, CommandInterface command, String... args) {
    try {
      String result = command.execute(args);
      subscriber.onNext(result);
    } catch (Exception e) {
      if (!subscriber.isDisposed()) {
        subscriber.onError(e);
      }
    }
  }
}
