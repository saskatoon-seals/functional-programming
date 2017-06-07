package cleanresource;

public class Client {
  private Job job;

  public Client(Job job) {
    this.job = job;
  }

  public Job makeRequest() {
    System.out.println("Making request");

    return job;
  }

  public void close(String jobId) {
    System.out.printf(
        "Closing job: %s.",
        jobId
    );
  }
}
