package camel;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Assert;
import org.junit.Test;

import common.BeanstalkUtil;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

public class ReactiveConsumerTest extends CamelTestSupport {
  private static final int REPEAT_COUNT = 30;
  private static final String STREAM_NAME = "pub";
  private static final String TUBE_NAME = "testTube";

  @Test
  public void collectsItemsFromTimerSource() throws Exception {
    new RouteBuilder() {
      @Override
      public void configure() throws Exception {
          from("timer:tick?period=5&repeatCount=" + REPEAT_COUNT)
            .setBody()
            .header(Exchange.TIMER_COUNTER)
            .to("reactive-streams:" + STREAM_NAME);
      }
    }.addRoutesToCamelContext(context);
    context.start();

    TestObserver<Integer> testObserver = new TestObserver<>();
    Observable.fromPublisher(
        CamelReactiveStreams
          .get(context)
          .fromStream(STREAM_NAME, Integer.class)
    )
    .zipWith(
        Observable.range(1, REPEAT_COUNT),
        (x, y) -> y
    )
    .blockingSubscribe(testObserver);

    testObserver.assertComplete();
    testObserver.assertNoErrors();
    testObserver.assertValueCount(REPEAT_COUNT);
  }

  //NOTE: Listening to beanstalk is indefinite -> no complete event!
  @Test
  public void collectsMessagesFromBeanstalkSource() throws Exception {
    int numOfMessages = 2;

    new RouteBuilder() {
      @Override
      public void configure() throws Exception {
        from("beanstalk:" + TUBE_NAME)
          .to("reactive-streams:" + STREAM_NAME);
      }
    }.addRoutesToCamelContext(context);
    context.start();

    byte[] ales = new byte[] {65, 76, 69, 83};
    byte[] maja = new byte[] {77, 65, 74, 65};
    BeanstalkUtil.cleanTube(TUBE_NAME);
    BeanstalkUtil.sendMessage(TUBE_NAME, ales, maja);

    CountDownLatch latch = new CountDownLatch(numOfMessages);
    TestObserver<byte[]> testObserver = new TestObserver<>();
    Observable.fromPublisher(
        CamelReactiveStreams
          .get(context)
          .fromStream(STREAM_NAME, byte[].class)
    )
    .doOnNext(x -> latch.countDown())
    .subscribe(testObserver);

    latch.await(5, TimeUnit.SECONDS);

    testObserver.assertNotComplete();
    testObserver.assertNoErrors();
    Assert.assertArrayEquals(
        ales,
        testObserver.values().get(0)
    );
    Assert.assertArrayEquals(
        maja,
        testObserver.values().get(1)
    );
  }
}
