package handlingerrors;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.nio.client.HttpAsyncClient;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

/**
 * Creates observable instance for HTTP async request from Apache library
 *
 * @author ales (only refactored and adopted to RxJava 2.0)
 *
 * @param <T>
 */
public class ObservableHttp<T> {

  private final ObservableOnSubscribe<T> onSubscribe;

  private ObservableHttp(ObservableOnSubscribe<T> onSubscribe) {
      this.onSubscribe = onSubscribe;
  }

  private static <T> ObservableHttp<T> create(ObservableOnSubscribe<T> onSubscribe) {
    return new ObservableHttp<T>(onSubscribe);
  }

  public Observable<T> toObservable() {
    return Observable.create(observer -> onSubscribe.subscribe(observer));
  }

  public static ObservableHttp<ObservableHttpResponse> createGet(
      String uri, final HttpAsyncClient client) {
    return createRequest(new HttpGet(uri), client);
  }

  public static ObservableHttp<ObservableHttpResponse> createRequest(
      final HttpUriRequest httpRequest, final HttpAsyncClient client) {

    //Subscribe method makes an asynchronous http requests and registers callback on observer
    return ObservableHttp.create(observer
        -> client.execute(httpRequest, new HttpResponseFutureCallback(observer)));
  }

  //************************************************************************************************
  //                                      Helper classes
  //************************************************************************************************

  //Registers http response callback on observer ("blocking subscribe" is the observer)
  private static class HttpResponseFutureCallback implements FutureCallback<HttpResponse> {
    private ObservableEmitter<ObservableHttpResponse> observer;

    private HttpResponseFutureCallback(ObservableEmitter<ObservableHttpResponse> observer) {
      this.observer = observer;
    }

    @Override
    public void completed(HttpResponse result) {
        observer.onNext(
            new ObservableHttpResponse(result,
                ResponseConsumerBasic.createContentObservable(result)));

        observer.onComplete();
    }

    @Override
    public void failed(Exception ex) {
        observer.onError(ex);
    }

    @Override
    public void cancelled() {
        observer.onComplete();
    }
  }
}
