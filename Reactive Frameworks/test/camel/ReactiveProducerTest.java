package camel;

import java.util.Arrays;
import java.util.List;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.junit.Test;
import org.reactivestreams.Subscriber;

import io.reactivex.Flowable;

public class ReactiveProducerTest extends CamelTestSupport {
  private static final String TARGET = "target";
  private static final String STREAM_NAME = "sub";
  private static final List<String> MESSAGES = Arrays.asList("Luka", "Maja", "Ales");

  @Test
  public void subscribesToSourceObservable() throws Exception {
    //Prepare
    MockEndpoint target = getMockEndpoint("mock:" + TARGET);
    target.expectedBodiesReceived(MESSAGES);

    new RouteBuilder() {
      @Override
      public void configure() throws Exception {
        from("reactive-streams:" + STREAM_NAME)
          .to("mock:" + TARGET);
      }
    }.addRoutesToCamelContext(context);

    //Execute
    Subscriber<String> subscriber = CamelReactiveStreams
        .get(context)
        .streamSubscriber(STREAM_NAME, String.class);

    Flowable
      .fromIterable(MESSAGES)
      .subscribe(subscriber);

    //Verify
    target.assertIsSatisfied();
  }
}
