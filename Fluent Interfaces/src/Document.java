import java.util.function.Function;

public class Document<T> {

	String name;
	T value;
	Function<T, String> toString;
	Function<String, T> fromString;
	
	public Document() {
		
	}
	
	public Document(String name, T value, Function<T, String> toString, Function<String, T> fromString) {
		this.name = name;
		this.value = value;
		this.toString = toString;
		this.fromString = fromString;
	}
	
	public String getName() {
		return name;
	}
	
	public Document<T> named(String name) {
		this.name = name;
		return this;
	}
	
	public T getValue() {
		return value;
	}
	
	public Document<T> value(T value) {
		this.value = value;
		return this;
	}
	
	public Function<T, String> getToString() {
		return toString;
	}
	
	public Document<T> toStringConverter(Function<T, String> toString) {
		this.toString = toString;
		return this;
	}
	
	public Function<String, T> getFromString() {
		return fromString;
	}
	
	public Document<T> fromStringConverter(Function<String, T> fromString) {
		this.fromString = fromString;
		return this;
	}	
	
	public void setName(String name) {
		this.name = name;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public void setToString(Function<T, String> toString) {
		this.toString = toString;
	}

	public void setFromString(Function<String, T> fromString) {
		this.fromString = fromString;
	}
}
