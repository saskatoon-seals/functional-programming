package mapping;

import datastructures.TreeBuilder;

public class MapReduce {

	public static int getModifiedSum() {
		return TreeBuilder.build()
		                  .getStream()
		                  .map(node -> node.incrementValue(1))
		                  .mapToInt(node -> (int) node.getValue())
		                  .reduce(0, (sum, value) -> sum + value);
	}
}
