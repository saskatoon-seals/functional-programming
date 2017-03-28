package vcommondemo;

import java.util.NoSuchElementException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Reinvention of an Optional monad with minimum functionality that still satisfies the
 * monad laws:
 *  1. Left Identity
 *  2. Right Identity
 *  3. Associativity
 *
 * @param <T> - value to wrap into the context of a monad
 */
public class Optional<T> {
  private T value;

  private Optional() {
    this(null);
  }

  private Optional(T value) {
    this.value = value;
  }

  //************************************************************************************************
  //                                            Monad APIs
  //************************************************************************************************

  /**
   * Map operation (Functor)
   *
   * Maps a function over a functor's value
   *
   * @param mapper - function to map over
   * @return Optional with a mapped value
   */
  public <R> Optional<R> map(Function<T, R> mapper) {
    return execute(
        () -> Optional.of(mapper.apply(value))
    );
  }

  /**
   * Compose apply (Applicative functor)
   *
   * NOTE: Can't be used in a fluent (applicative style) way
   *
   * @param functor - functor with a function as it's value
   * @return Optional with a mapped value
   */
  public <R> Optional<R> composeApply(Optional<Function<T, R>> functor) {
    if (functor.isEmpty()) {
      return empty();
    }

    return map(functor.get());
  }

  /**
   * Apply operation (Applicative functor)
   *
   * NOTE: Compiler doesn't know weather a functor has a function or data - it can't prevent
   * a runtime error of trying to feed data to data
   *
   * @param functor - Optional functor
   * @return - new Optional functor
   * @exception ClassCastException - if the current optional contains data instead of a function
   */
  @SuppressWarnings("unchecked")
  public <U, R> Optional<R> apply(Optional<U> functor) {
    return execute(
        () -> functor.map((Function<U, R>) value)
    );
  }

  /**
   * FlatMap or bind operation (Monad)
   *
   * @param mapper - mapper that accepts a value and returns an optional
   * @return optional with preserved context (emptiness)
   */
  public <R> Optional<R> flatMap(Function<T, Optional<R>> mapper) {
    return execute(
        () -> mapper.apply(value)
    );
  }

  //************************************************************************************************
  //                                      Non-monad APIs
  //************************************************************************************************

  public T get() {
    if (isEmpty()) {
      throw new NoSuchElementException();
    }

    return value;
  }

  public boolean isEmpty() {
    return value == null;
  }

  //************************************************************************************************
  //                                       Static APIs
  //************************************************************************************************

  public static <T> Optional<T> empty() {
    return new Optional<>();
  }

  public static <T> Optional<T> of(T value) {
    return new Optional<>(value);
  }

  //************************************************************************************************
  //                                       Helper methods
  //************************************************************************************************

  private <R> Optional<R> execute(Supplier<Optional<R>> supplier) {
    if (isEmpty()) {
      return empty();
    }

    return supplier.get();
  }
}
