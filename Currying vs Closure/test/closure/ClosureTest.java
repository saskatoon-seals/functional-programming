package closure;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.function.Function;

import org.junit.Test;

public class ClosureTest {

	@Test
	public void getStringOperationReturnsExpectedResult() {
		Closure closure = new Closure();		
		Function<String, String> closureFunction = closure.getStringOperation();
		
		assertThat(closureFunction.apply("Ales"), is("ales:4:4"));
	}
	
	@Test
	public void canModifyInstanceVariable() {
		Closure closure = new Closure();		
		Function<String, String> closureFunction = closure.getStringOperation();
		
		closureFunction.apply("Ales");
		
		assertThat(closure.getInstanceLength(), is(4));
	}
}
