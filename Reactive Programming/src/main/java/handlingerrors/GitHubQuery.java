package handlingerrors;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;

import com.google.gson.Gson;

import common.ObserverUtil;
import io.reactivex.Observable;

public class GitHubQuery implements Runnable {
  private final String username;

  public GitHubQuery(String username) {
    this.username = username;
  }

  @Override
  public void run() {
    try(CloseableHttpAsyncClient client = HttpAsyncClients.createDefault()) {
      client.start();

      @SuppressWarnings("rawtypes")
      Observable<Map> response = githubUserInfoRequest(client);

      ObserverUtil.blockingSubscribePrint(
          response.map(json -> json.get("name") + "(" + json.get("language") + ")"),
          "Json response");
    } catch(IOException e) {
      e.printStackTrace();
    }
  }

  //************************************************************************************************
  //                                  Helper methods
  //************************************************************************************************

  private Observable<Map> githubUserInfoRequest(CloseableHttpAsyncClient client) {
    //Makes an actual HTTP request
    Observable<Map> responseJson =
        makeJsonRequest(client, "https://api.github.com/users/" + username + "/repos");

    //Only responses with git_url represent a github repository
    return responseJson.filter(json -> json.containsKey("git_url")) //Represents GitHub repository
                       .filter(json -> json.get("fork").equals(false)); //Non-forked repositories
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private Observable<Map> makeJsonRequest(HttpAsyncClient client, String url) {
    Observable<String> rawResponse = createRawResponse(client, url);

    Observable<String> jsonObjects = toJsonObject(rawResponse);
    Observable<String> jsonArrays = rawResponse.filter(data -> data.startsWith("["));

    Observable<Map> response =
        jsonArrays.ambWith(jsonObjects) //we'll use the one emitting data and treat result as JSON array
                  .map(GitHubQuery::jsonToListOfMaps) //turn into List of Map instances
                  .flatMapIterable(list -> list) //flattens the lists of maps into maps only
                  .cast(Map.class);

    return response;
  }

  private Observable<String> createRawResponse(HttpAsyncClient client, String url) {
    return ObservableHttp.createGet(url, client)
                         .toObservable()
                         .flatMap(GitHubQuery::toJsonResponse) //gets JSON string of an HTTP response
                         .retry(5) //Possible problems with connecting to GitHub
                         .cast(String.class) //?
                         .map(String::trim); //Trims trailing and leading white spaces
  }

  private static Observable<String> toJsonObject(Observable<String> jsonObservable) {
    return jsonObservable.filter(data -> data.startsWith("{"))
                         .map(data -> "[" + data + "]");
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static List<Map> jsonToListOfMaps(String jsonData) {
    return new Gson().fromJson(jsonData, List.class);
  }

  private static Observable<String> toJsonResponse(ObservableHttpResponse response) {
    return response.getContent()
                   .map(bytes -> new String(bytes,
                                            java.nio.charset.StandardCharsets.UTF_8));
  }

  //************************************************************************************************
  //                                  Main method
  //************************************************************************************************

  public static void main(String... args){
    GitHubQuery client = new GitHubQuery("meddle0x53");

    client.run();
  }
}
