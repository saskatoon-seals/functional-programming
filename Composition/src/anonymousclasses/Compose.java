package anonymousclasses;

public class Compose {

	public interface CompositionFunction<T, R> {
		R call(T t);
	}
	
	public static <T, U, R> CompositionFunction<T, R> compose(CompositionFunction<U, R> f,
															  CompositionFunction<T, U> g) {
		return new CompositionFunction<T, R>() {
			//Anonymous inner class
			public R call(T t) {
				return f.call(g.call(t));	
			}
		};
	}
}
