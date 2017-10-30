package creatingobservables.customscheduler;

import static java.util.concurrent.Executors.newFixedThreadPool;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.reactivex.Scheduler;
import io.reactivex.schedulers.Schedulers;

public class Util {
  
  public static void log(Object label) {
    System.out.println(
        System.currentTimeMillis() + "\t| " +
        Thread.currentThread().getName()   + "\t| " +
        label);
  }

  static ExecutorService poolA = newFixedThreadPool(10, threadFactory("Sched-A-%d"));
  static Scheduler schedulerA = Schedulers.from(poolA);

  ExecutorService poolB = newFixedThreadPool(10, threadFactory("Sched-B-%d"));
  Scheduler schedulerB = Schedulers.from(poolB);

  ExecutorService poolC = newFixedThreadPool(10, threadFactory("Sched-C-%d"));
  Scheduler schedulerC = Schedulers.from(poolC);

  private static ThreadFactory threadFactory(String pattern) {
      return new ThreadFactoryBuilder()
          .setNameFormat(pattern)
          .build();
  }
}
