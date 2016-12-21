package reactivesum.imperative;

import java.io.BufferedReader;
import java.io.IOException;

import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class StdInObservable implements ObservableOnSubscribe<String>{
  
  BufferedReader reader;
  
  public StdInObservable(BufferedReader reader) {
    this.reader = reader;
  }

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
