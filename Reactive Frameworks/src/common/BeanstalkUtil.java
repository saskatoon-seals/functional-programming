package common;

import java.util.Arrays;
import java.util.Optional;

import org.apache.camel.component.beanstalk.ConnectionSettings;

import com.surftools.BeanstalkClient.Client;
import com.surftools.BeanstalkClient.Job;

public final class BeanstalkUtil {

  private BeanstalkUtil() { }

  public static void sendMessage(String tubeName, byte[]... messages) {
    Client client = new ConnectionSettings(tubeName)
        .newWritingClient();

    Arrays.stream(messages)
          .forEach(message -> client.put(0L, 0, 5, message));

    client.close();
  }

  public static Optional<byte[]> readMessage(String tubeName) {
    return readMessageHelper(tubeName, 0);
  }

  public static Optional<byte[]> readMessageBlocking(String tubeName) {
    return readMessageHelper(tubeName, null);
  }

  public static void cleanTube(String tubeName) {
    Client client = new ConnectionSettings(tubeName)
        .newReadingClient(false);

    Job job = client.reserve(0);
    while(job != null) {
      client.delete(job.getJobId());
      job = client.reserve(0);
    }
  }

  private static Optional<byte[]> readMessageHelper(String tubeName, Integer timeout) {
    Client client = new ConnectionSettings(tubeName)
        .newReadingClient(false);

    Job job = client.reserve(timeout);

    Optional.empty();

    if (job != null) {
      client.delete(job.getJobId());
    }

    client.close();

    return job == null ? Optional.empty() : Optional.of(job.getData());
  }
}
