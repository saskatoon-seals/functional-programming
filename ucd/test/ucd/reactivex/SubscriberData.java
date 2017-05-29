package ucd.reactivex;

import java.util.LinkedList;
import java.util.List;

import io.reactivex.ObservableEmitter;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Cancellable;

public class SubscriberData<T>  implements ObservableEmitter<T> {
  private List<T> results = new LinkedList<>();
  private Throwable error = null;
  private boolean completed = false;

  public List<T> getResults() {
    return results;
  }

  public Throwable getError() {
    return error;
  }

  public boolean isCompleted() {
    return completed;
  }

  @Override
  public void onNext(T data) {
    results.add(data);
  }

  @Override
  public void onError(Throwable e) {
    error = e;
  }
  @Override
  public void onComplete() {
    completed = true;
  }

  @Override
  public void setDisposable(Disposable d) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setCancellable(Cancellable c) {
    // TODO Auto-generated method stub

  }

  @Override
  public boolean isDisposed() {
    // TODO Auto-generated method stub
    return false;
  }

  @Override
  public ObservableEmitter<T> serialize() {
    // TODO Auto-generated method stub
    return null;
  }
}
