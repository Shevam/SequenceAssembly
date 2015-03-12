package overlapGraph;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class OverlapGraph {
	private static int minimumOverlapLength;
	private LinkedHashMap<String, Node> nodeList;
	private HashMap<Node, LinkedList<DirectedEdge>> adjacencyList;
	private static final OverlapGraph instance = new OverlapGraph(minimumOverlapLength);
	
	public static synchronized OverlapGraph getInstance()
	{
	    return instance;
	}
	
	public OverlapGraph(int minimumOverlapLength) {
		OverlapGraph.minimumOverlapLength = minimumOverlapLength;
        nodeList = new LinkedHashMap<String, Node>();
        adjacencyList = new HashMap<Node, LinkedList<DirectedEdge>>();
    }
	
	public Node addNode(String value) {
		if (nodeList.containsKey(value))
			return nodeList.get(value);
	    Node newNode = new Node(value);
	    nodeList.put(value, newNode);
	    
	    for(Node node : nodeList.values())
	    	checkForOverlapsAndAddEdges(node, newNode);
	    
	    return newNode;
	}
	
	public void checkForOverlapsAndAddEdges(Node node, Node newNode) {
		String overlap;
		if(node.getRead().contains(newNode.getRead().substring(0, minimumOverlapLength-1)))
		{
			overlap = OverlapGraphStaticMethods.getOverlap(node.getRead(), newNode.getRead(), minimumOverlapLength);
			if(overlap != null)
				addEdge(node, newNode, overlap);
		}
		if(newNode.getRead().contains(node.getRead().substring(0, minimumOverlapLength-1)))
		{
			overlap = OverlapGraphStaticMethods.getOverlap(newNode.getRead(), node.getRead(), minimumOverlapLength);
			if(overlap != null)
				addEdge(newNode, node, overlap);
		}
	}

	public DirectedEdge addEdge(Node from, Node to, String suffixToPrefix) {
		LinkedList<DirectedEdge> edgeList;
		DirectedEdge newEdge;
		
        if (from == null || to == null || suffixToPrefix.equals(""))
        	return null;
        
        edgeList = adjacencyList.get(from);
        
        if (edgeList == null) {
			adjacencyList.put(from, new LinkedList<DirectedEdge>());
			edgeList = adjacencyList.get(from);
		}
        else {
        	for(DirectedEdge edge : edgeList)
            	if(edge.getEnd() == to)
            		return edge;
        }
        
    	newEdge = new DirectedEdge(from, to, suffixToPrefix);
        edgeList.add(newEdge);
        
        return newEdge;
	}
	
	public LinkedHashMap<String, Node> getNodeList() {
		return nodeList;
	}
	
	public LinkedList<DirectedEdge> getAdjacencyList(Node aNode) {
		return adjacencyList.get(aNode);
	}
	
	public HashMap<Node, LinkedList<DirectedEdge>> getAdjacencyList() {
		return adjacencyList;
	}
	
	public int getOverlapLength(Node node1, Node node2) {
		for (DirectedEdge edge : adjacencyList.get(node1)) {
			if(edge.getEnd() == node2)
				return edge.getOverlapLength();
		}
		return 0;
	}
}
