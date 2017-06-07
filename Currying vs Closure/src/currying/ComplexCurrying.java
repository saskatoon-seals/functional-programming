package currying;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

//data can't be mixed together with function definition
//function accepting 2 parameters is simpler than curried function
public class ComplexCurrying {
  public static void method() {
    curriedHelper(
        client -> msg -> client.send(msg), //function definition
        () -> new Client(),                //data parameter 1
        "314"                              //data parameter 2
    );

    nonCurriedHelper(
        (client, msg) -> client.send(msg), //function definition
        () -> new Client(),                //data parameter 1
        "314"                              //data parameter 2
    );
  }

  private static void curriedHelper(Function<Client, Function<String, Integer>> action,
      Supplier<Client> factory, String msg) {
    System.out.println(
      action.apply(factory.get())
            .apply(msg)
    );
  }

  private static void curriedHelper2(Function<Supplier<Client>, Function<String, Integer>> action,
      Supplier<Client> factory, String msg) {
    System.out.println(
        action.apply(factory).apply(msg)
    );
  }

  private static void nonCurriedHelper(BiFunction<Client, String, Integer> action, Supplier<Client> factory,
      String msg) {
    System.out.println(
      action.apply(
          factory.get(),
          msg
      )
    );
  }

  public static void main(String... args) {
    method();
  }
}
