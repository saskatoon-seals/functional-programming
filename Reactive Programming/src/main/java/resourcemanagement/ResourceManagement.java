package resourcemanagement;

import java.io.IOException;
import java.util.concurrent.Callable;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;

import handlingerrors.ObservableHttp;
import handlingerrors.ObservableHttpResponse;
import io.reactivex.Observable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;

public class ResourceManagement {
  public Observable<ObservableHttpResponse> request(String url) {
    //Factory method to create closable http async client
    Callable<CloseableHttpAsyncClient> resourceFactory = () -> {
      CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
      client.start();

      System.out.println(
          Thread.currentThread().getName() +
          " : Created and started the client.");
      return client;
    };

    //Uses the client instance and the url to construct the resulting Observable instance
    Function<HttpAsyncClient, Observable<ObservableHttpResponse>> observableFactory = (client) -> {
      System.out.println(
          Thread.currentThread().getName() +
          " : About to create Observable.");

      return ObservableHttp.createGet(url, client)
                           .toObservable();
    };

    //Closes the client resource
    Consumer<CloseableHttpAsyncClient> disposeAction = (client) -> {
      try {
        System.out.println(Thread.currentThread().getName() + " : Closing the client.");
        client.close();
      } catch (IOException e) {
      }
    };

    /*
     * After the observable is returned, none of the passed in lambdas is invoked.
     * Subscribing will call the resourceFactory
     */
    return Observable.using(
        resourceFactory,
        observableFactory,
        disposeAction);
  }

  /*
   * Process:
   *  1. Allocate the resource
   *  2. Create the Observable instance
   *  3. Close the resource
   */
  public void run() {
    String url = "https://api.github.com/orgs/ReactiveX/repos";
    Observable<ObservableHttpResponse> response = request(url);

    System.out.println("Not yet subscribed.");

    /*
     * Converts the raw ObservableHttpResponse object to string (still no resources are allocated
     * and no request is sent).
     */
    Observable<String> stringResponse =
        response.flatMap(resp -> resp.getContent()
                .map(bytes -> new String(bytes, java.nio.charset.StandardCharsets.UTF_8)))
                .retry(5)
                .map(String::trim)
                /*
                 * Re-use the resource (no need to make 2 resource allocations) for the
                 * future subscribers.
                 * On subscription, instead of requesting the remote server again, cached data is
                 * reused.
                 */
                .cache();

    System.out.println("Subscribe 1:");
    //After data is fetched the subscriber is automatically unsubscribed, http client is disposed.
    System.out.println(stringResponse.blockingFirst());

    System.out.println("Subscribe 2:");
    //Second subscription
    System.out.println(stringResponse.blockingFirst());
  }

  public static void main(String... args) {
    new ResourceManagement().run();
  }
}
