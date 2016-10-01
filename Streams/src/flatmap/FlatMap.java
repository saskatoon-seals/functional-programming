package flatmap;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import datastructures.Node;
import datastructures.TreeBuilder;

public class FlatMap {

    public static void execute() {
        List<Node> nodes1 = TreeBuilder.build().getStream()
                .collect(Collectors.toList());

        List<Node> nodes2 = TreeBuilder.build().getStream()
                .map(node -> node.incrementValue(1))
                .filter(node -> (int) node.getValue() > 4)
                .collect(Collectors.toList());
        
        List<List<Node>> nodesCombined = Arrays.asList(nodes1, nodes2);
                
        nodesCombined.stream() //creates Stream<List<Node>>
                     .flatMap(FlatMap::process)
                     .map(node -> node.getValue()) //creates Stream<Integer>
                     .distinct()
                     .forEach(System.out::println);
    }
    
    private static Stream<Node> process(List<Node> nodes) {
        return nodes.stream()
                    .map(node -> {
                        node.incrementValue(-1);
                        return node;
                    })
                    .filter(node -> (int) node.getValue() > 4);
    }
}
