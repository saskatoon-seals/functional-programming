package cleanresource;

public class Job {
  private final String id;
  private final int data;

  public Job(String id, int data) {
    this.id = id;
    this.data = data;
  }

  public String getId() {
    return id;
  }

  public int getData() {
    return data;
  }
}
