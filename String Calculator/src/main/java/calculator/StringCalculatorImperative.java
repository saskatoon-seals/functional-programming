package calculator;

import static calculator.Constants.ERROR_MESSAGE;
import static calculator.Constants.REGEX;

public class StringCalculatorImperative {
  private static String sum(String... values) {
    if (values.length == 1) {
      try {
        return String.valueOf(
            Integer.parseInt(
                values[0]
            )
        );
      } catch (NumberFormatException e) {
        return ERROR_MESSAGE;
      }
    }

    int sum = 0;

    for (String value : values) {
      try {
        sum += Integer.parseInt(value);
      } catch (NumberFormatException e) {}
    }

    return String.valueOf(sum);
  }

  /**
   * Calculates sum
   *
   * @param input - input string of numbers
   * @return sum of numbers
   */
  public static String sum(String input) {
    if (input == null || input.isEmpty()) {
      return ERROR_MESSAGE;
    }

    return sum(input.split(REGEX));
  }
}
