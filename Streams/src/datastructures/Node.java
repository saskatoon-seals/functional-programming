package datastructures;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Node {

	String name;
	Object value;
	
	Node parent;
	List<Node> children = new ArrayList<>();
	
	public Node(String name, Object value) {
		this(name, value, null);
	}
	
	public Node(String name, Object value, Node parent) {
		this.name = name;
		this.value = value;
		this.parent = parent;
	}
	
	public String getName() {
		return name;
	}
	
	public Object getValue() {
		return value;
	}
	
	public String getParentName() {
		return parent == null ? "" : parent.name;
	}
	
	public Node incrementValue(Integer value) {
		return new Node(this.name, Integer.sum((Integer)this.value, value));
	}
	
	Node addChild(String name, Object value) {
		return addChild(new Node(name, value, this));
	}
	
	Node addChild(Node child) {
		children.add(child);
		return this;
	}
	
	private List<Node> getNodes() {
		List<Node> nodes = new ArrayList<>();
		
		nodes.add(this);
		for (Node child : children) {
			nodes.addAll(child.getNodes());
		}
		
		return nodes;
	}
	
	public Stream<Node> getStream() {
		return getNodes().stream();
	}
	
	@Override
	public String toString() {
		return String.format("%s: %s", name, value.toString());
	}
	
//	public static Collector<T, ?, Map<String, Object>> getNodeMapCollector() {
//		return Collectors.toMap(node -> node.getName(), node -> node.getValue());
//	}
}
