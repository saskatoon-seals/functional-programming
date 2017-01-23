package calculator;

import static calculator.StringCalculatorFunctional.sum;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class StringCalculatorFunctionalTest {

  @Test
  public void returnsErrorMessageWhenGivenEmptyInput() {
    assertEquals(
        "Calculation failed",
        sum("")
    );
  }

  @Test
  public void returnsEqualityWhenElementaryInputOfTypeIntegerGiven() {
    assertEquals(
        "314",
        sum("314")
    );
  }

  @Test
  public void returnsErrorMessageWhenCorruptedInputGiven() {
    assertEquals(
        "Calculation failed",
        sum("bad string")
    );
  }

  @Test
  public void returnsSumOfTwoIntegers() {
    assertEquals(
        "10",
        sum("6, 4")
    );
  }

  @Test
  public void returnsSumOfTwoIntegersIgnoringSingleCorruptedInput() {
    assertEquals(
        "13",
        sum("2, 11, bad string")
    );
  }

  @Test
  public void returnsSumOfMultipleIntegersIgnoringMultipleCorruptedInputs() {
    assertEquals(
        "40",
        sum("1, bad input, 20, 10, bad input, bad input, 9")
    );
  }
}
