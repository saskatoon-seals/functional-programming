package reactivesum.imperative;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;

public class ReactiveSum implements Observer<Double> {
  private double sum;
  
  public ReactiveSum(Observable<Double> a, Observable<Double> b) {
    Observable.combineLatest(a, b, new BiFunction<Double, Double, Double>() {
      @Override
      public Double apply(Double x, Double y) {
        return x + y;
      }
    })
    .subscribe(this);
  }

  @Override
  public void onSubscribe(Disposable d) {
    
  }

  @Override
  public void onNext(Double sum) {
    this.sum = sum;    
    System.out.println("Sum: " + sum);
  }

  @Override
  public void onError(Throwable e) {
    e.printStackTrace();
  }

  @Override
  public void onComplete() {
    System.out.println("Exiting. Last sum was: " + sum);
  }
}
