package handlingerrors;

import org.apache.http.HttpResponse;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;

public class ResponseConsumerBasic {

  public static Observable<byte[]> createContentObservable(HttpResponse response) {
    return Observable.create(new ObservableOnSubscribe<byte[]>() {
      @Override
      public void subscribe(ObservableEmitter<byte[]> o) throws Exception {
        long length = response.getEntity().getContentLength();
        if (length > Integer.MAX_VALUE) {
            o.onError(new IllegalStateException("Content Length too large for a byte[] => " + length));
        } else {
            ExpandableByteBuffer buf = new ExpandableByteBuffer((int) length);
            try {
                buf.consumeInputStream(response.getEntity().getContent());
                o.onNext(buf.getBytes());
                o.onComplete();
            } catch (Throwable e) {
                o.onError(e);
            }
        }
      }
    });
  }
}
