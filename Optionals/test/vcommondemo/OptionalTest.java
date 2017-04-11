package vcommondemo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.function.Function;

import org.junit.Test;

public class OptionalTest {

  //************************************************************************************************
  //                                      Map tests (Functor)
  //************************************************************************************************

  @Test
  public void mapReturnsFunctorWithConcatenatedWords() {
    assertEquals(
        "Ales loves functors.",
        Optional.of("Ales")
                .map(value -> value.concat(" loves functors."))
                .get()
    );
  }

  @Test
  public void mapReturnsFunctorWithSum() {
    assertEquals(
        new Integer(3),
        Optional.of(13)
                .map(value -> value - 10)
                .get()
    );
  }

  @Test
  public void returnsEmptyWhenMappingOverEmptyFunctor() {
    Optional<Double> x = Optional.empty();

    Optional<Double> y = x.map(value -> 0.5 * value);

    assertTrue(x.isEmpty());
    assertTrue(y.isEmpty());
  }

  //************************************************************************************************
  //                                      ComposeApply tests (Applicative)
  //************************************************************************************************

  @Test
  public void appliesAdditionToFunctorWithNumber() {
    assertEquals(
        new Integer(12),
        Optional.of(9)
                .composeApply(Optional.of(value -> value + 3))
                .get()
    );
  }

  @Test
  public void appliesEmptyFunctionPreservesEmptiness() {
    assertTrue(
        Optional.of(9)
                .composeApply(Optional.empty())
                .isEmpty()
    );
  }

  @Test
  public void appliesFunctionOnEmptyApplicativePreservesEmptiness() {
    assertTrue(
        Optional.<Integer>empty()
                .composeApply(Optional.of(value -> value + 3))
                .isEmpty()
    );
  }

  //pure(x -> y -> y / x) <*> a <*> b
  @Test
  public void chainsApplicationOfFunctionOnTwoFunctors() {
    assertEquals(
        new Double(2.5),
        Optional.of(10.0)
                .composeApply(
                    Optional.of(4)
                            .composeApply(
                                Optional.of(x -> y -> y / x)
                            )
                )
                .get()
    );
  }

  @Test
  public void chainsApplicationOfFunctionOnEmptyAndNonEmptyFunctor() {
    Optional<Integer> a = Optional.empty();
    Optional<Double> b = Optional.of(10.0);

    assertTrue(
        b.composeApply(
            a.composeApply(
                Optional.of(x -> y -> y / x)
            )
         )
         .isEmpty()
    );
  }

  @Test
  public void chainsApplicationOfFunctionOnNonEmptyAndEmptyFunctor() {
    Optional<Integer> a = Optional.of(4);
    Optional<Double> b = Optional.empty();

    assertTrue(
        b.composeApply(
            a.composeApply(
                Optional.of(x -> y -> y / x)
            )
         )
         .isEmpty()
    );
  }

  //************************************************************************************************
  //                                      Apply tests (Applicative)
  //************************************************************************************************
  Function<Integer, Integer> mapper = value -> value + 10;
  Function<Integer, Function<Integer, Integer>> curriedMapper = a -> b -> a - b;

  @Test
  public void appliesSingleFunctionOnFunctor() {
    assertEquals(
        new Integer(110),
        Optional.of(mapper)
                .apply(Optional.of(100))
                .get()
    );
  }

  @Test
  public void chainsFuntionApplicationsOnTwoFunctors() {
    assertEquals(
        new Integer(190),
        Optional.of(curriedMapper)
                .apply(Optional.of(200))
                .apply(Optional.of(10))
                .get()
    );
  }

  @Test
  public void chainsFunctionApplicationOnEmptyAndNonEmptyFunctors() {
    assertTrue(
        Optional.of(curriedMapper)
                .apply(Optional.empty())
                .apply(Optional.of(10))
                .isEmpty()
    );
  }

  @Test(expected=ClassCastException.class)
  public void throwsWhenApplyingFunctorOnNonApplicative() {
    Optional.of(mapper)
            .apply(Optional.of(100))  //Applicative becomes a normal functor (with data value)
            .apply(Optional.of(100)); //At this point the optional doesn't contain a function anymore
  }

  //************************************************************************************************
  //                                      flatMap tests (Monad)
  //************************************************************************************************

  @Test
  public void flatMapReturnsOptionalWithNumberDivision() {
    assertEquals(
        new Double(2.5),
        Optional.of(10.0)
                .flatMap(value -> Optional.of(value / 4))
                .get()
    );
  }

  @Test
  public void flatMapConcatenatesStrings() {
    assertEquals(
        "Ales loves monads.",
        Optional.of("Ales")
                .flatMap(value -> Optional.of(value.concat(" loves monads.")))
                .get()
    );
  }

  @Test
  public void chainsFlatMapOperations() {
    assertEquals(
        new Integer(20),
        Optional.of(1)
                .flatMap(value -> Optional.of(value + 10))
                .flatMap(value -> Optional.of(value + 9))
                .get()
    );
  }

  @Test
  public void flatMapPreservesContext() {
    assertTrue(
        Optional.of("Ales")
                .flatMap(value -> Optional.of(value.concat(" Ursic")))
                .flatMap(value -> Optional.<String>empty())
                .flatMap(value -> Optional.of(value.concat(" loves monads.")))
                .isEmpty()
    );
  }
}
