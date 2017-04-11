package walktheline;

import static org.junit.Assert.assertEquals;
import static walktheline.Main.FAILED_MSG;
import static walktheline.Main.LAND_LEFT;
import static walktheline.Main.LAND_RIGHT;
import static walktheline.Main.MAX_DIFFERENCE;
import static walktheline.Main.walk;

import org.junit.Test;

public class MainTest {
  private static final int[] START = new int[]{0, 0};

  @Test
  public void returnsStartingPositionWhenNoActionsGiven() {
    assertEquals(
        "left: 0, right: 0",
        walk(new int[]{0, 0})
    );
  }

  @Test
  public void failsWhenStartingPositionInvalid() {
    assertEquals(
        FAILED_MSG,
        walk(new int[]{MAX_DIFFERENCE + 1, 0})
    );
  }

  @Test
  public void returnsEndPositionAfterSingleWalkStep() {
    assertEquals(
        "left: 2, right: 0",
        walk(START, new int[]{LAND_LEFT, 2})
    );
  }

  @Test
  public void returnsEndPositionAfterThreeSteps() {
    assertEquals(
        "left: 4, right: 3",
        walk(START, new int[]{LAND_LEFT, 2}, new int[]{LAND_RIGHT, 3}, new int[]{LAND_LEFT, 2})
    );
  }

  @Test
  public void failsAfterTwoSteps() {
    assertEquals(
        FAILED_MSG,
        walk(START, new int[]{LAND_LEFT, 2}, new int[]{LAND_RIGHT, 1}, new int[]{LAND_LEFT, 3})
    );
  }

  @Test
  public void returnsFailureAfterTryingToWalkAfterFailing() {
    assertEquals(
        FAILED_MSG,
        walk(START, new int[]{LAND_LEFT, 2}, new int[]{LAND_RIGHT, 1}, new int[]{LAND_LEFT, 3},
            new int[]{LAND_RIGHT, 1})
    );
  }
}
