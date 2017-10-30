package schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import common.ThreadUtil;
import io.reactivex.Scheduler;
import io.reactivex.Scheduler.Worker;
import io.reactivex.schedulers.Schedulers;

/** Description of behaviour of different schedulers:
 *
 *    1. newThread(): will spawn a thread instance for every sub-task.
 *    2. trampoline(): schedules sub-task and the parent task before executing them (while tasks
 *      are being created, they are also executing, that's why the removal sub-tasks can execute,
 *      because the add sub-tasks filled the list).
 */
public class SchedulersTypes {

  /**
   * Schedules tasks on a worker thread and multiple subtasks within a task on child workers.
   * Tasks add a set of random numbers to a list and then it removes them.
   *
   * @param scheduler - scheduler instance
   * @param numberOfSubTasks - number of subtasks
   * @param onTheSameWorker - single or multi-threaded system
   */
  public static void schedule(Scheduler scheduler, int numberOfSubTasks, boolean onTheSameWorker) {
    //Stack variables, they'll vary for different threads
    List<Integer> list = new ArrayList<>(0);
    AtomicInteger current = new AtomicInteger(0);

    Random random = new Random();
    Worker worker = scheduler.createWorker();

    //Adds a random number to a list of numbers (subtask) [executed later - lazy evaluation]
    Runnable addWork = () -> {
      synchronized (list) {
        System.out.println("  Add : " + Thread.currentThread().getName() + " " + current.get());
        list.add(random.nextInt(current.get()));
        System.out.println("  End add : " + Thread.currentThread().getName() + " " + current.get());
      }
    };

    //Removes the first number from the list if it's not empty (subtask)
    Runnable removeWork = () -> {
      synchronized (list) {
        if (!list.isEmpty()) {
          System.out.println("  Remove : " + Thread.currentThread().getName());
          list.remove(0);
          System.out.println("  End remove : " + Thread.currentThread().getName());
        }
      }
    };

    //Parent work task that contains "add work" and "remove work" subtasks.
    Runnable work = () -> {
      //First print that executes:
      System.out.println(Thread.currentThread().getName());

      /*
       * Adds "add work" tasks to either the same worker thread or creates a new worker thread and
       * schedules there.
       */
      for (int i = 1; i <= numberOfSubTasks; i++) {
        current.set(i);

        System.out.println("Begin add!");
        if (onTheSameWorker) {
          worker.schedule(addWork);
        } else {
          scheduler.createWorker().schedule(addWork);
        }
        System.out.println("End add!");
      }

      /*
       * Adds "remove work" tasks to the same worker or creates a new one.
       *
       * While the main task is executing, this list is still empty, so while loop never enters.
       */
      while (!list.isEmpty()) {
        System.out.println("Begin remove!");

        if (onTheSameWorker) {
          worker.schedule(removeWork);
        } else {
          scheduler.createWorker().schedule(removeWork);
        }

        System.out.println("End remove!");
      }
    };

    worker.schedule(work);
  }

  public static void main(String... args) {
    schedule(Schedulers.trampoline(), 2, false);

    ThreadUtil.delayMillis(1000);
  }
}
