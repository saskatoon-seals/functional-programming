package camel;

import org.apache.camel.CamelContext;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.reactive.streams.api.CamelReactiveStreams;
import org.apache.camel.impl.DefaultCamelContext;

import io.reactivex.Observable;

public final class CamelUtil {
  private CamelUtil() { }

  public static CamelContext createCamelContext(Route... endpoints) throws Exception {
    CamelContext context = new DefaultCamelContext();

    new RouteBuilder() {
      @Override
      public void configure() throws Exception {
        for (Route endpoint : endpoints) {
          from(endpoint.source).to(endpoint.destination);
        }
      }
    }
    .addRoutesToCamelContext(context);

    return context;
  }

  public static <T> Observable<T> createObservable(CamelContext context, String streamName, Class<T> klass) {
    return Observable.fromPublisher(
        CamelReactiveStreams
        .get(context)
        .fromStream(streamName, klass)
    );
  }
}
