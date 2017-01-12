package handlingerrors;

import org.apache.http.HttpResponse;

import io.reactivex.Observable;

public class ObservableHttpResponse {

  private final HttpResponse response;
  private final Observable<byte[]> contentSubscription;

  public ObservableHttpResponse(HttpResponse response, Observable<byte[]> contentSubscription) {
      this.response = response;
      this.contentSubscription = contentSubscription;
  }

  /**
   * The {@link HttpResponse} returned by the Apache client at the beginning of the response.
   *
   * @return {@link HttpResponse} with HTTP status codes, headers, etc
   */
  public HttpResponse getResponse() {
      return response;
  }

  /**
   * If the response is not chunked then only a single array will be returned. If chunked then multiple arrays.
   */
  public Observable<byte[]> getContent() {
      return contentSubscription;
  }

}
