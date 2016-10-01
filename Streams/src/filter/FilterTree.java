package filter;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import datastructures.Node;
import datastructures.TreeBuilder;

public class FilterTree {

	public static void execute() {
		Node tree = TreeBuilder.build();
		
		Stream<Node> nodeStream = tree.getStream();
		
		nodeStream.filter(node -> node.getParentName().equals("OUTPUT"))
		          .forEach(System.out::println);
	}
	
	/*
	 * You can easily build your own collectors inside Node.java.
	 */
	public static Map<String, Object> getNodesMessage(String parentName) {
		return TreeBuilder.build()
				          .getStream()
		                  .filter(node -> node.getParentName().equals(parentName))
		                  .collect(Collectors.toMap(node -> node.getName(), node -> node.getValue()));
	}
}
