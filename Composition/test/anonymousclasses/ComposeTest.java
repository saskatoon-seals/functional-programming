package anonymousclasses;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import anonymousclasses.Compose.CompositionFunction;

public class ComposeTest {

	@Test
	public void composesFunctions() {
		CompositionFunction<Integer, Integer> doubleNumber = 
				new CompositionFunction<Integer, Integer>() {
			public Integer call(Integer t) {
				return 2 * t;
			}
		};
		
		CompositionFunction<Integer, Integer> negate = 
				new CompositionFunction<Integer, Integer>() {
			public Integer call(Integer t) {
				return -t;
			}
		};
		
		CompositionFunction<Integer, Integer> negateDoubleNumber = 
				Compose.compose(negate, doubleNumber);
		
		assertThat(negateDoubleNumber.call(10), is(-20));
	}
}
