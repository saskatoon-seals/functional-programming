package reactivesum.imperative;

import static reactivesum.imperative.FactoryMethods.from;
import static reactivesum.imperative.FactoryMethods.varStream;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;

public class Client {
  ConnectableObservable<String> input;
  Observable<Double> a, b;
  
  ReactiveSum sum;
  
  public Client() {
    input = from(System.in);
    
    a = varStream("a", input);
    b = varStream("b", input);
    
    sum = new ReactiveSum(a, b);
  }
  
  /**
   *  Starts listening to input stream
   */
  public void run() {
    input.connect();
  }
  
  /**
   * Invokes client
   * 
   * @param args - CLI args (ignored)
   */
  public static void main(String... args) {
    Client client = new Client();
    client.run();
  }
}
