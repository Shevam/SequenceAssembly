package graph.overlap;
import interfaces.IGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public abstract class OverlapGraph implements IGraph {
	private static int minimumOverlapLength;
	private LinkedHashMap<String, Node> nodeList;
	private HashMap<Node, LinkedList<DirectedEdge>> adjacencyList;
	private Node leastIndegreeNode;
	
	protected OverlapGraph(int minimumOverlapLength) {
		OverlapGraph.minimumOverlapLength = minimumOverlapLength;
        nodeList = new LinkedHashMap<String, Node>();
        adjacencyList = new HashMap<Node, LinkedList<DirectedEdge>>();
    }
	
	protected Node addNode(String value) {
		if (nodeList.containsKey(value))
			return nodeList.get(value);
	    Node newNode = new Node(value);
	    nodeList.put(value, newNode);
	    
	    for(Node node : nodeList.values())
	    	checkForOverlapsAndAddEdges(node, newNode);
	    
	    if(leastIndegreeNode == null)
	    	leastIndegreeNode = newNode;
	    else
	    	if(newNode.getIndegree()<leastIndegreeNode.getIndegree())
	    		leastIndegreeNode = newNode;
	    
	    return newNode;
	}
	
	protected DirectedEdge addEdge(Node from, Node to, String suffixToPrefix) {
		LinkedList<DirectedEdge> edgeList;
		DirectedEdge newEdge;
		
        if (from == null || to == null || suffixToPrefix.equals(""))
        	return null;
        
        edgeList = adjacencyList.get(from);
        
        if (edgeList == null) {
			adjacencyList.put(from, new LinkedList<DirectedEdge>());
		}
        else {
        	for(DirectedEdge edge : edgeList)
            	if(edge.getEnd() == to)
            		return edge;
        }
        
    	newEdge = new DirectedEdge(from, to, suffixToPrefix);
        adjacencyList.get(from).add(newEdge);
        
        return newEdge;
	}
	
	protected void checkForOverlapsAndAddEdges(Node node, Node newNode) {
		String overlap;
		if(node.getRead().contains(newNode.getRead().substring(0, minimumOverlapLength-1)))
		{
			overlap = this.getOverlap(node.getRead(), newNode.getRead(), minimumOverlapLength);
			if(overlap != null)
				addEdge(node, newNode, overlap);
		}
		if(newNode.getRead().contains(node.getRead().substring(0, minimumOverlapLength-1)))
		{
			overlap = this.getOverlap(newNode.getRead(), node.getRead(), minimumOverlapLength);
			if(overlap != null)
				addEdge(newNode, node, overlap);
		}
	}
	
	protected LinkedHashMap<String, Node> getNodeList() { return nodeList;	}
	
	protected LinkedList<DirectedEdge> getAdjacencyList(Node aNode) { return adjacencyList.get(aNode);	}
	
	protected HashMap<Node, LinkedList<DirectedEdge>> getAdjacencyList() {	return adjacencyList; }
	
	protected int getOverlapLength(Node node1, Node node2) {
		for (DirectedEdge edge : adjacencyList.get(node1)) {
			if(edge.getEnd() == node2)
				return edge.getOverlapLength();
		}
		return 0;
	}
	
	protected String getOverlap(String startString, String endString, int minimumOverlapLength) {
		int endIndex = endString.length() - 1;
		while (endIndex >= minimumOverlapLength	&& !startString.endsWith(endString.substring(0, endIndex)))
			endIndex--;
		if(!startString.endsWith(endString.substring(0, endIndex)))
			return null;
		return endString.substring(0, endIndex);
	}
	
	protected Node getUnvisitedNeighbour(Node aNode) 
    {
		LinkedList<DirectedEdge> edgeList;
		
		edgeList = this.getAdjacencyList(aNode);
		if(edgeList != null)
		{
			for (DirectedEdge edge : edgeList) {
				if(!edge.getEnd().isVisited())
					return edge.getEnd();
			}
		}
		return null;
    }
	
	protected Node getLeastIndegreeUnvisitedNode()
	{
		if(!leastIndegreeNode.isVisited())
			return leastIndegreeNode;
		else
		{
			Node leastIndegreeUnvisitedNode = null;
			for (Node node : nodeList.values()) {
				if(node.isVisited())
					continue;
				
				if (leastIndegreeUnvisitedNode == null)
					leastIndegreeUnvisitedNode = node;
				else
					if(node.getIndegree() < leastIndegreeUnvisitedNode.getIndegree())
						leastIndegreeUnvisitedNode = node;
			}
			return leastIndegreeUnvisitedNode;
		}
	}
	
	protected Node getNextNodeWithHighestOverlapLength(Node currentNode) {
		
		DirectedEdge edgeWithHighestOverlapLength = null;
		if(adjacencyList.get(currentNode) == null)
			return null;
		
		for (DirectedEdge edge : adjacencyList.get(currentNode)) {
			if(edge.getEnd().isVisited())
				continue;
			
			if(edgeWithHighestOverlapLength == null)
				edgeWithHighestOverlapLength = edge;
			else
				if(edge.getOverlapLength()>edgeWithHighestOverlapLength.getOverlapLength())
					edgeWithHighestOverlapLength = edge;
		}
		
		if(edgeWithHighestOverlapLength == null)
			return null;
		else
			return edgeWithHighestOverlapLength.getEnd();
	}
	
	protected void printContigInFastaFormat(BufferedWriter writer, LinkedList<Node> contigNodeList, int contigCount) {
		
		try{
			int writerRemainingLineSpace = 0;
			String contigPart;
			
        	writer.write(">c" + contigCount + "_NodeCount_"+ contigNodeList.size() +"\n");
			contigPart = contigNodeList.getFirst().getRead();
			
			for(int i=0;i<contigPart.length();i+=fastaLineLength) {
				if(i+fastaLineLength > contigPart.length()) {
					writer.write(contigPart.substring(i));
					writerRemainingLineSpace = fastaLineLength - (contigPart.length() - i);
				}
				else
					writer.write(contigPart.substring(i, i+fastaLineLength) + "\n");
			}
			
			for(int j=1; j<contigNodeList.size(); j++)
			{
				contigPart = contigNodeList.get(j).getRead().substring(this.getOverlapLength(contigNodeList.get(j-1), contigNodeList.get(j)));
				
				if(contigPart.length() > writerRemainingLineSpace) {
					writer.write(contigPart.substring(0, writerRemainingLineSpace) + "\n");
					for(int i=writerRemainingLineSpace;i<contigPart.length();i+=fastaLineLength) {
						if(contigPart.length() < i+fastaLineLength) {
							writer.write(contigPart.substring(i));
							writerRemainingLineSpace = fastaLineLength - (contigPart.length() - i);
						}
						else
							writer.write(contigPart.substring(i, i+fastaLineLength) + "\n");
					}
				}
				else {
					writer.write(contigPart);
					writerRemainingLineSpace -= contigPart.length();
				}
			}
			writer.flush();
			writer.newLine();
		}
		catch (IOException e) {
			System.err.println("OverlapGraph:printContigInFastaFormat: error while writing to file");
		}
	}

	public void displayGraph() {
		System.out.println("Graph adjacency List:");
		LinkedList<DirectedEdge> edgeList;
		for (Node node : nodeList.values()) {
			System.out.print(node.getRead() + " --> ");
			edgeList = adjacencyList.get(node);
			if (edgeList!=null) {
				for (DirectedEdge edge : edgeList) {
					if (edge!=null)
						System.out.print(edge.getEnd().getRead() + "[" + edge.getOverlapLength() + "], ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public abstract void constructGraph(File readsFile, int minimumOverlapLength);
	public abstract void traverseGraphToGenerateContigs(String outputFile);
}

