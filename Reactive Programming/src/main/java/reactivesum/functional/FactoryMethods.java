package reactivesum.functional;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.observables.ConnectableObservable;

public class FactoryMethods {

  /**
   * Creates connectable observable from emitter source - stdin
   * 
   * @param stream - stdin emitter
   * @return connectable observable
   */
  static ConnectableObservable<String> from(final InputStream stream) {
    return from(new BufferedReader(new InputStreamReader(stream)));
  }
  
  static ConnectableObservable<String> from(final BufferedReader reader) {
    return Observable.create(new StdInObservable(reader))
                     .publish();
  }
  
  /**
   * Creates observable from a given observable source by propagating only the content of interest
   * defined by the variable name
   * 
   * @param varName - variable name
   * @param input - observable source
   * @return observable
   */
  static Observable<Double> varStream(final String varName, Observable<String> input) {
    final Pattern pattern = Pattern.compile("^\\s*" + varName + "\\s*[:|=]\\s*(-?\\d+\\.?\\d*)$");

    return input.map(pattern::matcher)
                .filter(matcher -> matcher.matches() && matcher.group(1) != null)
                .map(matcher -> Double.parseDouble(matcher.group(1)));
  }
  
  /**
   * Observer that subscribes to 2 observables.
   * 
   * @param a - observable
   * @param b - observable
   */
  public static void reactiveSum(Observable<Double> a, Observable<Double> b) {
    Observable.combineLatest(a, b, (x, y) -> x + y)
              .subscribe(
                  sum -> System.out.println("Sum: " + sum),
                  error -> error.printStackTrace(),
                  () -> System.out.println("Exiting"));
  }
}
