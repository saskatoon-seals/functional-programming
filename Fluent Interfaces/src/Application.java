import java.util.ArrayList;
import java.util.List;

public class Application {
	
	private List<Document<Integer>> documents = new ArrayList<>();

	public Application() {
		//Old school (with constructor) [not knowing what belongs to which attribute]
		Document<Integer> document = new Document<Integer>("System Design Document",
										 531000,
										 value -> value.toString(),
										 value -> Integer.parseInt(value));
		documents.add(document);
		
			
		//Old school (with setters) [repetition of "document" keyword]
		document = new Document<Integer>();
		
		document.setName("System Design Document");
		document.setValue(5310000);
		document.setToString(value -> value.toString());
		document.setFromString(value -> Integer.parseInt(value));
		
		documents.add(document);
		
			
		//Only with fluent interfaces [no repetition, 3 step process]
		document = new Document<>();
		
		document.named("System Design Document")
				.value(5310000)
				.toStringConverter(value -> value.toString())
				.fromStringConverter(value -> Integer.parseInt(value));
		
		documents.add(document);
		
		
		//With fluent interface and anonymous inner class - two steps 
		//[clear division of process in 2 steps, if multiple elements => repetition of "add(document)"]
		document = new Document<Integer>() {
			{
				named("System Design Document");
				value(5310000);
				toStringConverter(value -> value.toString());
				fromStringConverter(value -> Integer.parseInt(value));
			}
		};
		
		documents.add(document);
		
		
		//With fluent interface and anonymous inner class - single step 
		//[no repetition for multiple elements, less readable?]
		documents.add(new Document<Integer>() {
			{
				named("System Design Document");
				value(5310000);
				toStringConverter(value -> value.toString());
				fromStringConverter(value -> Integer.parseInt(value));
			}			
		});
	}
}
