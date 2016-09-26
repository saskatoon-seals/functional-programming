package currying;

import static org.junit.Assert.*;

import java.util.function.BiFunction;
import java.util.function.Function;

import static org.hamcrest.CoreMatchers.*;

import org.junit.Test;

import currying.Currying;

public class CurryingTest {
	
	private static final String[] INPUT = {"Ales", "Maja"};
	private static final String EXPECTED_OUTPUT = "Ales + Maja";

	@Test
	public void concatenateWithoutCurrying() {
		assertThat(Currying.concatenate(INPUT[0], INPUT[1]), 
				   is(EXPECTED_OUTPUT));
	}
	
	@Test
	public void concatenateWithCurrying() {
		assertThat(Currying.concatenateWithCurrying(INPUT[0], INPUT[1]), 
				   is(EXPECTED_OUTPUT));
	}
	
	@Test
	public void curryingConcatenateWithHelperMethod() {
		assertThat(Currying.curryingConcatenateWithHelperFunction(INPUT[0], INPUT[1]), 
				   is(EXPECTED_OUTPUT));
	}
	
	@Test
	public void getConcatenate() {
		BiFunction<String, String, String> function = Currying.getConcatenate();
		
		assertThat(function.apply(INPUT[0], INPUT[1]), is(EXPECTED_OUTPUT));
	}
	
	@Test
	public void getConcatenateWithCurrying() {
		Function<String, Function<String, String>> function = Currying.getConcatenateWithCurrying();
		
		assertThat(function.apply(INPUT[0]).apply(INPUT[1]), is(EXPECTED_OUTPUT));
	}
}
