package camel;

import java.util.Optional;

import org.apache.camel.Produce;
import org.apache.camel.ProducerTemplate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import common.BeanstalkUtil;

//Mocks source endpoint and actually puts jobs to beanstalk
public class BeanstalkProducerTest extends CamelTestSupport {
  private static final String TUBE_NAME = "testTube";
  private static final String MESSAGE = "Hello, world!";
  private static final String DIRECT_SOURCE = "direct:start";

  //Fake source endpoint
  @Produce(uri = DIRECT_SOURCE)
  private ProducerTemplate direct;

  @Before
  public void setup() {
    BeanstalkUtil.cleanTube(TUBE_NAME);
  }

  @Test
  public void writesMessageToBeanstalk() throws Exception {
    direct.sendBody(MESSAGE);

    Optional<byte[]> result = BeanstalkUtil.readMessage(TUBE_NAME);

    assertNotEquals(Optional.empty(), result);
    assertEquals(
        MESSAGE,
        new String(result.get())
    );
  }

  //Method under test
  @Override
  protected RouteBuilder createRouteBuilder() {
      return new RouteBuilder() {
          @Override
          public void configure() {
              from(DIRECT_SOURCE)
                .to("beanstalk:" + TUBE_NAME); //Beanstalk producer under test
          }
      };
  }
}
