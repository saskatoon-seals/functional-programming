package camel;

import static camel.CamelUtil.createCamelContext;
import static camel.ReactiveBeanstalk.getConsumerRoute;
import static camel.ReactiveBeanstalk.getProducerRoute;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.camel.CamelContext;
import org.apache.camel.component.beanstalk.ConnectionSettingsFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import common.BeanstalkUtil;
import io.reactivex.Flowable;
import io.reactivex.subscribers.TestSubscriber;

public class ReactiveBeanstalkTest {
  private static final byte[] MESSAGE = new byte[] {65, 76, 69, 83};
  private static final String SOURCE_TUBE = "sourceTube";
  private static final String DESTINATION_TUBE = "destinationTube";

  private CamelContext context;

  @Before
  public void setup() throws Exception {
    context = createCamelContext(
        getConsumerRoute(SOURCE_TUBE),
        getProducerRoute(DESTINATION_TUBE)
    );

    BeanstalkUtil.cleanTube(SOURCE_TUBE);
    BeanstalkUtil.cleanTube(DESTINATION_TUBE);
  }

  @After
  public void teardown() throws Exception {
    context.stop();
  }

  @Test
  public void fullTransferWithTransformingMessage() throws Exception {
    //Prepare
    byte[] newMessage = new byte[] {1, 2, 3, 4};

    //Execute
    ReactiveBeanstalk
        .receiveJobData(context, SOURCE_TUBE)
        .map(message -> newMessage)
        .subscribe(ReactiveBeanstalk.createSubscriber(context));

    BeanstalkUtil.sendMessage(SOURCE_TUBE, MESSAGE);

    //Verify
    Thread.sleep(1500);

    assertArrayEquals(
        newMessage,
        BeanstalkUtil.readMessage(DESTINATION_TUBE).get()
    );
  }

  @Test
  public void fullTransferWithInjectedContext() throws Exception {
    //Execute
    ReactiveBeanstalk
      .receiveJobData(context, SOURCE_TUBE)
      .subscribe(ReactiveBeanstalk.createSubscriber(context));

    BeanstalkUtil.sendMessage(SOURCE_TUBE, MESSAGE);

    //Verify
    Thread.sleep(1500);

    assertArrayEquals(
        MESSAGE,
        BeanstalkUtil.readMessage(DESTINATION_TUBE).get()
    );
  }

  @Test
  public void nextJobIsPulledFromBeanstalkAfterLastJobWasProcessed() throws Exception {
    //Prepare
    int numOfMessages = 3;
    byte[] msg1 = new byte[]{1};
    byte[] msg2 = new byte[]{2};
    byte[] msg3 = new byte[]{3};
    AtomicInteger count = new AtomicInteger(numOfMessages);
    TestSubscriber<byte[]> testObserver = new TestSubscriber<>();
    context = createCamelContext(
        getConsumerRoute(SOURCE_TUBE)
    );

    //Execute
    ReactiveBeanstalk
      .receiveJobData(context, SOURCE_TUBE)
      .concatMap(message -> handleMessageAndVerifyBeanstalkTube(message, count))
      .subscribe(testObserver);

    BeanstalkUtil.sendMessage(SOURCE_TUBE, msg1, msg2, msg3);

    //Verify
    Thread.sleep(1500);

    testObserver.assertNoErrors();
    testObserver.assertValueCount(numOfMessages);
    testObserver.assertNotComplete();
    assertArrayEquals(
        msg1,
        testObserver.values().get(0)
    );
    assertArrayEquals(
        msg2,
        testObserver.values().get(1)
    );
    assertArrayEquals(
        msg3,
        testObserver.values().get(2)
    );
  }

  //************************************************************************************************
  //                                    Helper methods
  //************************************************************************************************

  private Flowable<byte[]> handleMessageAndVerifyBeanstalkTube(byte[] message,
      AtomicInteger count) throws IOException {
    verifyBeanstalkTube(count.decrementAndGet());

    //And the processing works as "echo"
    return Flowable.just(message);
  }

  private void verifyBeanstalkTube(int expectedCount) throws IOException {
    int maxNumberOfReserverJobs = 1;
    Map<String, String> tubeStats = ConnectionSettingsFactory
        .DEFAULT
        .parseUri(SOURCE_TUBE)
        .newReadingClient(false)
        .statsTube(SOURCE_TUBE);

    assertEquals(
        expectedCount,
        Integer.parseInt(tubeStats.get("current-jobs-ready"))
    );
    assertEquals(
        maxNumberOfReserverJobs,
        Integer.parseInt(tubeStats.get("current-jobs-reserved"))
    );
  }
}
