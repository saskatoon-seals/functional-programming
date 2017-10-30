package observables;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import org.junit.Test;

import common.CreateObservable;
import io.reactivex.Observable;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.TestScheduler;

public class CreateObservableIntervalTest {

  @Test
  public void testBehavesAsNormalIntervalWithOneGap() throws Exception {
    TestScheduler testScheduler = new TestScheduler();
    Observable<Long> interval = CreateObservable.interval(
        Arrays.asList(100L), TimeUnit.MILLISECONDS, testScheduler
        );

    TestObserver<Long> testObserver = new TestObserver<Long>();
    interval.subscribe(testObserver);

    testObserver.assertEmpty();

    testScheduler.advanceTimeBy(101L, TimeUnit.MILLISECONDS);
    testObserver.assertValues(0L);

    testScheduler.advanceTimeBy(101L, TimeUnit.MILLISECONDS);
    testObserver.assertValues(0L, 1L);

    testScheduler.advanceTimeTo(1L, TimeUnit.SECONDS);
    testObserver.assertValues(0L, 1L, 2L, 3L, 4L, 5L, 6L, 7L, 8L, 9L);
  }
}
