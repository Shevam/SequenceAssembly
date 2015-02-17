package ImprovedDeBruijnGraph;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public class DeBruijnGraph {
	LinkedHashMap<String, Node> nodeList;
	HashMap<String, LinkedList<DirectedEdge>> adjacencyList;
	int kmerSize;
	private static final DeBruijnGraph instance = new DeBruijnGraph();
	
	public DeBruijnGraph() {
		super();
		nodeList = new LinkedHashMap<String, Node>();
		adjacencyList = new HashMap<String, LinkedList<DirectedEdge>>();
	}
	
	public static synchronized DeBruijnGraph getInstance()
	{
	    return instance;
	}
	
	public Node addNode(String km1mer) {
		if (nodeList.containsKey(km1mer))
			return nodeList.get(km1mer);
		Node newNode = new Node(km1mer);
		nodeList.put(km1mer, newNode);
		return newNode;
	}

	public DirectedEdge addEdge(String prefixString, String suffixString) {
		Node prefixNode, suffixNode;
		DirectedEdge newEdge;
		LinkedList<DirectedEdge> edgeList;

		prefixNode = nodeList.get(prefixString);
		if (prefixNode == null)
			prefixNode = addNode(prefixString);

		suffixNode = nodeList.get(suffixString);
		if (suffixNode == null)
			suffixNode = addNode(suffixString);
		
		edgeList = adjacencyList.get(prefixString);
		
		if (edgeList == null) {
			adjacencyList.put(prefixString, new LinkedList<DirectedEdge>());
			edgeList = adjacencyList.get(prefixString);
		}
		
		/*
		 * allowing duplicate edges
		
		else {
			for (DirectedEdge edge : edgeList) {
				if(edge.getEnd()==suffixNode)
					return edge;
			}
		}
		*/
		
		newEdge = new DirectedEdge(prefixNode, suffixNode);
		edgeList.add(newEdge);
		prefixNode.incrementOutdegree();
		suffixNode.incrementIndegree();
		prefixNode.addEdge(newEdge);
		
		return newEdge;
	}
	
	public int getKmerSize() { return this.kmerSize; }
	public void setKmerSize(int value) { this.kmerSize = value; }
	
	public void displayNodes() {
		System.out.println("Nodes (indeg, outdeg): ");
		for (Node node : nodeList.values()) {
			System.out.println(node.getKm1mer() + " (" + node.getIndegree() + ", " + node.getOutdegree() + ")");
		}
		System.out.println();
	}
	
	public void displayEdges() {
		System.out.println("Edges: ");
		for (String node : nodeList.keySet()) {
			if (adjacencyList.get(node) != null) {
				for (DirectedEdge edge : adjacencyList.get(node)) {
					edge.print();
				}
			}
		}
		System.out.println();
	}
	
	public void displayGraph() {
		System.out.println("Graph adjacency List:");
		for (String node : nodeList.keySet()) {
			System.out.print(node + " --> ");
			if (adjacencyList.get(node) != null) {
				for (DirectedEdge edge : adjacencyList.get(node)) {
					System.out.print(edge.getEnd().getKm1mer() + ", ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public DirectedEdge getUnvisitedEdge() {
		for (Node node : nodeList.values()) {
			DirectedEdge edge = node.getNextUnvisitedEdge();
			if (edge != null)
				return edge;
		}
		return null;
	}
	/*
	public DirectedEdge getUnvisitedEdge() {
		for (String nodeValue : nodeList.keySet()) {
			if (adjacencyList.get(nodeValue) != null) {
				for (DirectedEdge edge : adjacencyList.get(nodeValue)) {
					if (!edge.isVisited())
						return edge;
				}
			}
		}
		return null;
	}
	
	public synchronized DirectedEdge getUnvisitedEdge(Node currentNode) {
		if (adjacencyList.get(currentNode.getKm1mer()) != null) {
			for (DirectedEdge edge : adjacencyList.get(currentNode.getKm1mer())) {
				if (!edge.isVisited())
					return edge;
			}
		}
		return null;
	}
	*/
	public DirectedEdge getZeroInDegreeUnvisitedEdge() { // TODO: should find only zero-indegree node?
		for (String nodeValue : nodeList.keySet()) {
			if (adjacencyList.get(nodeValue) != null) {
				for (DirectedEdge edge : adjacencyList.get(nodeValue)) {
					if (!edge.isVisited() && (edge.getStart().getIndegree() == 0))
						return edge;
				}
			}
		}
		return null;
	}
}
