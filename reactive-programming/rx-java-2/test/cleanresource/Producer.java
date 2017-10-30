package cleanresource;

import io.reactivex.Observable;

public class Producer {
  //Winner
  public static Observable<Integer> send(Client client) {
    return Observable.<Integer, Job>using(
        () -> client.makeRequest(),
        job -> Observable.just(job.getData()),
        job -> client.close(job.getId())
    );
  }
}
