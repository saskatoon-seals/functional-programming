package reactivesum.imperative;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.reactivex.Observable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
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
    final Pattern pattern = Pattern.compile("\\^s*" + varName + "\\s*[:|=]\\s*(-?\\d+\\.?\\d*)$");
    
    return input.map(new Function<String, Matcher>() {
      @Override
      public Matcher apply(String str) {
        return pattern.matcher(str);
      }
    })
    .filter(new Predicate<Matcher>() {
      @Override
      public boolean test(Matcher matcher) {
        return matcher.matches() && matcher.group(1) != null;
      }
    })
    .map(new Function<Matcher, Double>() {
      @Override
      public Double apply(Matcher matcher) {
        return Double.parseDouble(matcher.group(1));
      }
    });
  }
}
