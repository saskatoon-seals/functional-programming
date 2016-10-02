/*
 * Implementation of echoing of typing with iterators and generators. 
 */

package infinitestreams;

import java.util.Scanner;
import java.util.stream.Stream;

public class Echo {
	
	public static void execute(boolean ITERATOR_OPTION) {
	
		Scanner scanner = new Scanner(System.in);
		
		if (ITERATOR_OPTION) {		
			//Iterator
			Stream.iterate(scanner.nextLine(), input -> scanner.nextLine())
				  .limit(10)
				  .forEach(System.out::println);
		} else {	
			//Generator
			Stream.generate(() -> scanner.nextLine())
			      .limit(10)
			      .forEach(System.out::println);
		}
		
		scanner.close();
	}
}
