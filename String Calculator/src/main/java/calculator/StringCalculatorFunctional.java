package calculator;

import static calculator.Constants.ERROR_MESSAGE;
import static calculator.Constants.REGEX;

import java.util.Arrays;
import java.util.Optional;

public class StringCalculatorFunctional {
  private static Optional<Integer> parseIntHelper(String number) {
    try {
      return Optional.of(Integer.parseInt(number));
    } catch (NumberFormatException e) {
      return Optional.empty();
    }
  }

  private static Optional<String> sum(String... values) {
    return Arrays
        .stream(values)
        .map(StringCalculatorFunctional::parseIntHelper)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .reduce(Integer::sum)
        .map(String::valueOf);
  }

  /**
   * Calculates sum
   *
   * @param input - input string of numbers
   * @return sum of numbers
   */
  public static String sum(String input) {
    return sum(input.split(REGEX))
        .orElse(ERROR_MESSAGE);
  }
}
