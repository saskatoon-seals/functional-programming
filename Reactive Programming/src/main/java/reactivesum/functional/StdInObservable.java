package reactivesum.functional;

import java.io.BufferedReader;
import java.io.IOException;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class StdInObservable implements ObservableOnSubscribe<String>{
  
  BufferedReader reader;
  
  public StdInObservable(BufferedReader reader) {
    this.reader = reader;
  }

  /**
   * This method is called by the reactiveSum observer/subscriber.
   * 
   * This method calls methods on the subscriber == sends notification messages.
   */
  @Override
  public void subscribe(ObservableEmitter<String> subscriber) throws Exception {
    if (subscriber.isDisposed()) {
      return;
    }    
    processInput(subscriber);
    
    if (!subscriber.isDisposed()) {
      subscriber.onComplete();
    }
  }   
  
  /**
   * Process CLI input and forwards it to the subscriber.
   * 
   * NOTE: The Emitter is an abstraction over Observer that handles cancellation for you.
   * 
   * @param subscriber - reactive sum
   */
  private void processInput(ObservableEmitter<String> subscriber) {
    try {
      String line;
      while(!subscriber.isDisposed() && (line = reader.readLine()) != null) {
        if (line == null || line == "exit") {
          break;
        }
        subscriber.onNext(line);
      }
    } catch (IOException e) {
      subscriber.onError(e);
    }
  }
}