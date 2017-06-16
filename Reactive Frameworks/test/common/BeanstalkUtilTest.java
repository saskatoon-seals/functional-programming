package common;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.Optional;

import org.junit.Before;
import org.junit.Test;

public class BeanstalkUtilTest {
  private static final String TUBE_NAME = "testTube";
  private static final byte[] MESSAGE = new byte[] {1, 2, 3, 4, 5};

  @Before
  public void setup() {
    BeanstalkUtil.cleanTube(TUBE_NAME);
  }

  @Test //tests both read and write methods together
  public void readsMessageSentToBeanstalk() {
    //Method no. 1 under test
    BeanstalkUtil.sendMessage(TUBE_NAME, MESSAGE);

    //Method no. 2 under test
    Optional<byte[]> result = BeanstalkUtil.readMessage(TUBE_NAME);

    assertNotEquals(Optional.empty(), result);
    assertArrayEquals(MESSAGE, result.get());
  }
}
