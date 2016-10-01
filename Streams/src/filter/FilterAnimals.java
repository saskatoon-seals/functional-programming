package filter;

import java.util.Arrays;
import java.util.stream.Stream;

public class FilterAnimals {

	private static final String[] ANIMALS = {"cats", "dog", "ox", "bats", "horses", "mule"};
	
	public static void execute() {
		Stream<String> animalsStream = Arrays.stream(ANIMALS);
		
		animalsStream.filter(animal -> animal.matches(".*s$"))
				     .sorted()
				     .forEach(System.out::println);
	}
}
