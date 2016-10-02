package datastructures;

public class TreeBuilder {

	public static Node build() {
		Node root = new Node("MAPPING", new Integer(1));
		
		Node rootChildA = new Node("INPUT", new Integer(2), root);
		Node rootChildB = new Node("OUTPUT", new Integer(3), root);
		
		root.addChild(rootChildA);
		root.addChild(rootChildB);
		
		rootChildA.addChild("A1", new Integer(4));
		rootChildA.addChild("A2", new Integer(5));
		rootChildB.addChild("B1", new Integer(6));
		rootChildB.addChild("B2", new Integer(7));
		
		return root;
	}
}
