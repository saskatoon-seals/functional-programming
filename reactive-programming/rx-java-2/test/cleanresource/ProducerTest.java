package cleanresource;

import org.junit.Before;
import org.junit.Test;

public class ProducerTest {
  Job job;
  Client client;

  @Before
  public void setup() {
    job = new Job("Ales", 1234);
    client = new Client(job);
  }

  @Test
  public void testSend() {
    Producer.send(client)
      .doOnNext(data -> System.out.println("Handling.."))
      .blockingSubscribe();
  }
}
