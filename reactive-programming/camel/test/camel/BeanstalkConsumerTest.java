package camel;

import org.apache.camel.CamelContext;
import org.apache.camel.EndpointInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.impl.DefaultCamelContext;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Before;
import org.junit.Test;

import common.BeanstalkUtil;

//Mocks destination endpoint and actually pulls jobs from beanstalk
public class BeanstalkConsumerTest extends CamelTestSupport {
  private static final String TUBE_NAME = "testTube";
  private static final byte[] MESSAGE = new byte[] {65, 76, 69, 83};
  private static final String DESTINATION_MOCK = "mock:result";

  //Fake destination endpoint
  @EndpointInject(uri = DESTINATION_MOCK)
  protected MockEndpoint resultEndpoint;

  @Before
  public void setup() {
    BeanstalkUtil.cleanTube(TUBE_NAME);
  }

  @Test
  public void readsMessageFromBeanstalk() throws Exception {
    CamelContext context = new DefaultCamelContext();
    context.addRoutes(
        createRouteBuilder()
    );

    resultEndpoint.expectedBodiesReceived(MESSAGE);

    context.start();
    BeanstalkUtil.sendMessage(TUBE_NAME, MESSAGE);
    context.stop();

    resultEndpoint.assertIsSatisfied();
  }

  //Method under test
  @Override
  protected RouteBuilder createRouteBuilder() {
      return new RouteBuilder() {
          @Override
          public void configure() {
              from("beanstalk:" + TUBE_NAME) //Beanstalk consumer under test
                .to(DESTINATION_MOCK);
          }
      };
  }
}
