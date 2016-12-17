package closure;

import java.util.function.Function;

public class Closure {
	//Can be modified (used for lambda expression)
	private int instanceLength;
	
	public int getInstanceLength() {
		return instanceLength;
	}
	
	//Enclosing context
	public Function<String, String> getStringOperation() {
		//Must be immutable
		final String separator = ":";
		
		return target -> {
			int localLength = target.length();
			instanceLength = target.length();
			
			return target.toLowerCase()
				   + separator + instanceLength + separator
				   + localLength;
		};
	}
}
