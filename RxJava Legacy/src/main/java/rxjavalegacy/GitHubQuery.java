package rxjavalegacy;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;

import com.google.gson.Gson;

import rx.Observable;
import rx.Subscription;
import rx.apache.http.ObservableHttp;
import rx.apache.http.ObservableHttpResponse;

//I'm using old RxJava 1.0 APIs
public class GitHubQuery implements Runnable {

  private final String username;
  private Map<String, Set<Map<String, Object>>> cache = new ConcurrentHashMap<>();

  public GitHubQuery(String username) {
    this.username = username;
  }

  @Override
  public void run() {
    try(CloseableHttpAsyncClient client = HttpAsyncClients.createDefault()) {
      client.start();

      @SuppressWarnings("rawtypes")
      Observable<Map> response = githubUserInfoRequest(client);

      blockingSubscribePrint(
          response.map(json -> json.get("name") + "(" + json.get("language") + ")"),
          "Json response");
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  //************************************************************************************************
  //                                  Instance helper methods
  //************************************************************************************************

  @SuppressWarnings({ "rawtypes" })
  private Observable<Map> githubUserInfoRequest(HttpAsyncClient client) {
    //Makes an actual HTTP request
    Observable<Map> responseJson =
        requestJson(client, "https://api.github.com/users/" + username + "/repos");

    //Only responses with git_url represent a github repository
    return responseJson.filter(json -> json.containsKey("git_url")) //Represents GitHub repository
                       .filter(json -> json.get("fork").equals(false)); //Non-forked repositories
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  /* default */ Observable<Map> requestJson(HttpAsyncClient client, String url) {
    Observable<String> rawResponse = createRawResponse(client, url);

    Observable<String> jsonObjects = toJsonObject(rawResponse);
    Observable<String> jsonArrays = rawResponse.filter(data -> data.startsWith("["));

    Observable<Map> response =
        jsonArrays.ambWith(jsonObjects) //we'll use the one emitting data and treat result as JSON array
                  .map(GitHubQuery::jsonToListOfMaps) //turn into List of Map instances
                  .flatMapIterable(list -> list) //flattens the lists of maps into maps only
                  .cast(Map.class)
                  .doOnNext(json -> getCache(url).add(json)); //Add to in-memory cache

    /*
     * fallback-to-cache mechanism:
     * If cache contains data, it will emit first and this data will be used instead
     */
    return Observable.amb(fromCache(url),
                          response);
  }

  //@TODO: Rewrite with RxJava 2.0
  private Observable<String> createRawResponse(HttpAsyncClient client, String url) {
    return ObservableHttp
            .createGet(url, client)
            .toObservable() //Makes async HTTP request using Apache HttpClient
            .flatMap(GitHubQuery::toJsonResponse) //gets JSON string of an HTTP response
            .retry(5) //Possible problems with connecting to GitHub
            .cast(String.class) //?
            .map(String::trim) //Trims trailing and leading white spaces
            .doOnNext(response -> getCache(url).clear()); //Clear cached info for this URL
  }

  public Set<Map<String, Object>> getCache(String url) {
    if (!cache.containsKey(url)) {
      cache.put(url, new HashSet<Map<String,Object>>());
    }
    return cache.get(url);
  }

  private Observable<Map<String, Object>> fromCache(String url) {
    return Observable.from(getCache(url)).defaultIfEmpty(null)
        .flatMap(json -> (json == null) ? Observable.never() : Observable.just(json))
        .doOnNext(json -> json.put("json_cached", true));
  }

  //************************************************************************************************
  //                                  Static helper methods
  //************************************************************************************************

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static List<Map> jsonToListOfMaps(String jsonData) {
    return new Gson().fromJson(jsonData, List.class);
  }

  private static Observable<String> toJsonObject(Observable<String> jsonObservable) {
    return jsonObservable.filter(data -> data.startsWith("{"))
                         .map(data -> "[" + data + "]");
  }

  private static Observable<String> toJsonResponse(ObservableHttpResponse response) {
    return response.getContent()
                   .map(bytes -> new String(bytes,
                                            java.nio.charset.StandardCharsets.UTF_8));
  }

  public static <T> void blockingSubscribePrint(Observable<T> observable, String name) {
    CountDownLatch latch = new CountDownLatch(1);

    subscribePrint(
        observable.finallyDo(() -> latch.countDown()),
        name);

    try {
      latch.await();
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

  public static <T> Subscription subscribePrint(Observable<T> observable, String name) {
    return observable.subscribe(
        (v) -> System.out.println(Thread.currentThread().getName()
            + "|" + name + " : " + v), (e) -> {
          System.err.println("Error from " + name + ":");
          System.err.println(e);
          System.err.println(Arrays
              .stream(e.getStackTrace())
              .limit(5L)
              .map(stackEl -> "  " + stackEl)
              .collect(Collectors.joining("\n"))
              );
        }, () -> System.out.println(name + " ended!"));
  }

  //************************************************************************************************
  //                                  Main method
  //************************************************************************************************

  public static void main(String... args){
    GitHubQuery client = new GitHubQuery("meddle0x53");
    client.run();
  }
}
