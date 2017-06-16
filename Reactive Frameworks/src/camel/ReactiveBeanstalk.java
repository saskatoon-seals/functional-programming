package camel;

import static camel.CamelUtil.createCamelContext;
import static camel.CamelUtil.createObservable;

import org.apache.camel.CamelContext;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.reactivestreams.Subscriber;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;

public class ReactiveBeanstalk {
  private static final String BEANSTALK_ENDPOINT = "beanstalk";
  private static final String REACTIVE_ENDPOINT = "reactive-streams";
  private static final String PUBLISH_STREAM = "publish";
  private static final String SUBSCRIBE_STREAM = "subscribe";

  public static void main(String[] args) throws Exception {
    String sourceTube = "sourceTube";
    String destinationTube = "destinationTube";

    CamelContext context = createCamelContext(
        getConsumerRoute(sourceTube),
        getProducerRoute(destinationTube)
    );

    receiveJobData(context, sourceTube)
      .repeat()
      .blockingSubscribe(createSubscriber(context));
  }

  public static Flowable<byte[]> receiveJobData(CamelContext context, String tubeName) {
    return createObservable(context, PUBLISH_STREAM, byte[].class)
        .doOnSubscribe(x -> context.start())
        .toFlowable(BackpressureStrategy.LATEST);
  }

  public static Subscriber<byte[]> createSubscriber(CamelContext context) {
    return CamelReactiveStreams
        .get(context)
        .streamSubscriber(SUBSCRIBE_STREAM, byte[].class);
  }

  public static Route getConsumerRoute(String tubeName) throws Exception {
    return new Route(
        BEANSTALK_ENDPOINT + ":" + tubeName,       //source
        REACTIVE_ENDPOINT + ":" + PUBLISH_STREAM   //destination
    );
  }

  public static Route getProducerRoute(String tubeName) throws Exception {
    return new Route(
        REACTIVE_ENDPOINT + ":" + SUBSCRIBE_STREAM, //source
        BEANSTALK_ENDPOINT + ":" + tubeName         //destination
    );
  }
}
