package graph.debruijn.improved;
import java.util.Iterator;
import java.util.LinkedList;

public class Node {

	private String km1mer;
	private int indegree, outdegree;
	private boolean edgeListVisited;
	private LinkedList<DirectedEdge> outgoingEdgeList, incomingEdgeList;
	
	public Node(String km1mer) {
		this.km1mer = km1mer;
		this.indegree = 0;
		this.outdegree = 0;
		this.edgeListVisited = false;
		this.outgoingEdgeList = new LinkedList<DirectedEdge>();
		this.incomingEdgeList = new LinkedList<DirectedEdge>();
	}
	
	public String getKm1mer() { return km1mer; }
	
	public void setKm1mer(String km1mer) { this.km1mer = km1mer; }
	public void displayKm1mer() { System.out.println(km1mer); }
	
	public int getIndegree() { return this.indegree; }
	public void incrementIndegree() { this.indegree++; }
	
	public int getOutdegree() { return this.outdegree; }
	public void incrementOutdegree() { this.outdegree++; }
	
	public boolean isBalanced() { return (indegree == outdegree); }
	public boolean isSemiBalanced() { return (Math.abs(indegree - outdegree) == 1); }
	
	public LinkedList<DirectedEdge> getOutgoingEdgeList() { return outgoingEdgeList; }
	public LinkedList<DirectedEdge> getincomingEdgeList() { return incomingEdgeList; }
	
	public DirectedEdge addEdgeTo(Node endNode) {
		DirectedEdge newEdge, currentEdge;
		
		for (Iterator<DirectedEdge> iter = outgoingEdgeList.iterator(); iter.hasNext();) {
			currentEdge = iter.next();
			if (endNode == currentEdge.getEnd()) {
				currentEdge.incrementWeight();
				this.incrementOutdegree();
				endNode.incrementIndegree();
				return currentEdge;
			}
		}
		
		newEdge = new DirectedEdge(this, endNode);
		outgoingEdgeList.add(newEdge);
		this.incrementOutdegree();
		endNode.incrementIndegree();
		return newEdge;
	}
	
	public void addIncomingEdge(DirectedEdge incomingEdge) {
		for (DirectedEdge edge : incomingEdgeList)
			if (edge == incomingEdge)
				return;
		this.incomingEdgeList.add(incomingEdge);
	}
	
	public DirectedEdge removeEdge(DirectedEdge edgeToRemove) {
		return outgoingEdgeList.remove(edgeToRemove) ? edgeToRemove : null;
	}
	
	public void printNode() {
		System.out.println(km1mer + " (" + indegree + ", " + outdegree + ")");
	}
	
	public synchronized DirectedEdge getUnvisitedIncomingEdge() {
		DirectedEdge edge;
		for (int i=0, n=incomingEdgeList.size(); i < n; i++) {
			edge = incomingEdgeList.get(i);
			if (!edge.isVisited())
				return edge;
		}
		return null;
	}
	
	public synchronized DirectedEdge getUnvisitedOutgoingEdge() {
		DirectedEdge edge;
		
		if (edgeListVisited)
			return null;
		
		//Collections.shuffle(edgeList);
		for (int i=0, n=outgoingEdgeList.size(); i < n; i++) {
			edge = outgoingEdgeList.get(i);
			if (!edge.isVisited())
				return edge;
		}
		
		edgeListVisited = true;
		return null;
	}
	
	public synchronized DirectedEdge getUnvisitedOutgoingEdge(DirectedEdge exception) {
		DirectedEdge edge;
		
		if (edgeListVisited)
			return null;
		
		for (int i=0, n=outgoingEdgeList.size(); i < n; i++) {
			edge = outgoingEdgeList.get(i);
			if (!edge.isVisited()) {
				if (edge != exception)
					return edge;
			}
		}
		
		if (exception != null)
			if(!exception.isVisited())
				return exception;
		
		edgeListVisited = true;
		return null;
	}
}