package observables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import common.CreateObservable;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;

public class ObservableTests {

  private Observable<String> tested;
  private List<String> expected;

  @Before
  public void setup() {
    tested = CreateObservable.sorted(
        (a, b) -> a.compareTo(b),
        "Star", "Bar", "Car", "War", "Far", "Jar");

    expected = Arrays.asList("Bar", "Car", "Far", "Jar", "Star", "War");
  }

  @Test
  public void sortedObservableEmitsItemsInAscendingOrder() {
    TestData data = new TestData();

    tested.subscribe(
        v -> data.appendItem(v),
        e -> data.setError(e),
        () -> data.setCompleted(true)
    );

    Assert.assertTrue(data.isCompleted());
    Assert.assertNull(data.getError());
    Assert.assertEquals(expected, data.getResult());
  }

  //Tests almost the same functionality as the test above without the TestData helper/fixture object
  @Test
  public void testUsingBlockingObservable() {
    List<String> result = tested.toList()
                                .blockingGet();

    Assert.assertEquals(expected, result);
  }

  @Test
  public void testUsingTestSubscriber() {
    TestObserver<String> observer = new TestObserver<String>();

    tested.subscribe(observer);
    //Is it necessary to manually unsubscribe? -> yes, if you use observer. no if you use subscriber
    observer.dispose();

    Assert.assertEquals(expected, observer.values());
    observer.assertComplete();
    observer.assertNoErrors();
    Assert.assertTrue(observer.isDisposed());
    observer.assertTerminated();
  }

  //************************************************************************************************
  //                                        Helpers
  //************************************************************************************************

  private class TestData {
    private Throwable error = null;
    private boolean completed = false;
    private List<String> result = new ArrayList<String>();

    public void setError(Throwable error) {
      this.error = error;
    }

    public boolean isCompleted() {
      return completed;
    }

    public void setCompleted(boolean completed) {
      this.completed = completed;
    }

    public List<String> getResult() {
      return result;
    }

    public void appendItem(String item) {
      result.add(item);
    }

    public Throwable getError() {
      return error;
    }
}
}
