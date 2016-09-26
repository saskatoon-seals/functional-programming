package currying;
import java.util.function.BiFunction;
import java.util.function.Function;

public class Currying {
		
	public static String concatenate(String input1, String input2) {
		BiFunction<String, String, String> concatenate = (x, y) -> String.format("%s + %s", x, y);
		
		return concatenate.apply(input1, input2);
	}
	
	public static String concatenateWithCurrying(String input1, String input2) {
		Function<String, Function<String, String>> curryingConcatenate = 
				x -> y -> String.format("%s + %s", x, y);
		
		return curryingConcatenate.apply(input1).apply(input2);
	}
	
	public static String curryingConcatenateWithHelperFunction(String input1, String input2) {
		BiFunction<String, String, String> helperFunction = (x, y) -> String.format("%s + %s", x, y);
		
		Function<String, Function<String, String>> curryingConcatenate = 
				x -> y -> helperFunction.apply(x, y);
				
		return curryingConcatenate.apply(input1).apply(input2);
	}
	
	public static BiFunction<String, String, String> getConcatenate() {
		return (x, y) -> String.format("%s + %s", x, y);
	}
	
	public static Function<String, Function<String, String>> getConcatenateWithCurrying() {
		return x -> y -> String.format("%s + %s", x, y);
	}
	
	public static Function<String, Function<String, String>> getConcatenateWithHelperFunction() {
		BiFunction<String, String, String> helperFunction = getConcatenate();
		
		return x -> y -> helperFunction.apply(x, y);
	}
}
