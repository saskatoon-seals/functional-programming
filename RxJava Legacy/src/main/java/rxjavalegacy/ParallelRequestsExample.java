package rxjavalegacy;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

import rx.Observable;
import rx.schedulers.Schedulers;


/**
 * Demonstrates parallelism by executing a number of requests in parallel.
 *
 * method within a flatMap creates a new observable instance for the every emitted item. We then
 * process that observable on a new thread to achieve a true parallelism.
 */
public class ParallelRequestsExample implements Runnable {

  @SuppressWarnings("rawtypes")
  @Override
  public void run() {
    CloseableHttpAsyncClient client = HttpAsyncClients.createDefault();
    GitHubQuery gitHubQuery = new GitHubQuery("meddle0x53");
    CountDownLatch latch = new CountDownLatch(1);

    try {
      client.start();

      //Request to the followers of meddle0x53
      Observable<Map> response =
          gitHubQuery.requestJson(client, "https://api.github.com/users/meddle0x53/followers");

      response.map(followerJson -> followerJson.get("url"))
              .cast(String.class)
              //new request for every follower will be executed in a separate scheduler in parallel
              .flatMap(profileUrl
                  -> gitHubQuery.requestJson(client, profileUrl)
                                .subscribeOn(Schedulers.io())
                                .filter(res -> res.containsKey("followers"))
                                .map(json -> json.get("login") +  " : " + json.get("followers"))
              )
              .doOnNext(follower -> System.out.println(follower))
              .count()
              .doOnCompleted(() -> latch.countDown())
              .subscribe(sum -> System.out.println("meddle0x53 : " + sum));

      try {
        latch.await();
      } catch (InterruptedException e) {}

    } finally {
      try {
        client.close();
      } catch (IOException e) {}
    }
  }

  public static void main(String[] args) {
    new ParallelRequestsExample().run();
  }

}
