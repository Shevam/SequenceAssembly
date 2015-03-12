package improvedDeBruijnGraph;
import java.util.LinkedList;

public class Node {

	private String km1mer;
	private int indegree, outdegree;
	private DoubleEndedLinkedList edgeList;
	private DirectedEdge nextUnvisitedEdge;
	private boolean locked;
	
	public Node(String km1mer) {
		this.km1mer = km1mer;
		this.indegree = 0;
		this.outdegree = 0;
		this.edgeList = new DoubleEndedLinkedList();
		this.nextUnvisitedEdge = null;
		this.locked = false;
	}
	
	public synchronized Node fetchNode() {
		if (locked)
			return null;
		else {
			locked = true;
			return this;
		}
	}
	
	public synchronized void releaseNode() { locked = false; }
	
	public String getKm1mer() { return km1mer; }
	public void setKm1mer(String km1mer) { this.km1mer = km1mer; }
	public void displayKm1mer() { System.out.println(km1mer); }

	public int getIndegree() { return this.indegree; }
	public void incrementIndegree() { this.indegree++; }
	
	public int getOutdegree() { return this.outdegree; }
	public void incrementOutdegree() { this.outdegree++; }
	
	public boolean isBalanced() { return (indegree == outdegree); }
	public boolean isSemiBalanced() { return (Math.abs(indegree - outdegree) == 1); }
	
	public DirectedEdge addEdge(DirectedEdge edge) {
		if (!edge.getStart().getKm1mer().equals(this.getKm1mer())) {
			return null; //TODO add exception (start incorrect)
		}

		if (!edgeList.isEmpty())
			edgeList.insert(edge);
		else {
			edgeList.insert(edge);
			nextUnvisitedEdge = edge;
		}
		return edge;
	}
	
	public DirectedEdge retrieveEdgeTo(Node endNode) {
		return edgeList.getEdgeTo(endNode);
	}
	
	public DirectedEdge removeEdge(DirectedEdge edgeToRemove) {
		DirectedEdge edgeRemoved = edgeList.remove(edgeToRemove);
		// TODO if NxtUnvEdge == removed then replace to 1st
		return edgeRemoved;
	}
	
	public DirectedEdge getNextUnvisitedEdge() { return nextUnvisitedEdge; }
	
	public DirectedEdge resetNextUnvisitedEdge() {
		while (nextUnvisitedEdge != null && nextUnvisitedEdge.isVisited()) {
			nextUnvisitedEdge = nextUnvisitedEdge.getNext();
		}
		return nextUnvisitedEdge;
	}
	
	/*
	public Edge getAnUnvisitedEdge() {
		Edge current = edgeList.getHead();
		while (current != null && current.isVisited()) {
			current = current.getNext();
		}
		return current;
	}
	*/
	
	public LinkedList<DirectedEdge> getUnvisitedEdges() {
		LinkedList<DirectedEdge> unvisitedEdgesList = new LinkedList<DirectedEdge>();
		DirectedEdge current = nextUnvisitedEdge; // TODO Used NUE here directly instead of head / check
		while (current != null && !current.isVisited()) {
			unvisitedEdgesList.add(current);
			current = current.getNext();
		}
		return unvisitedEdgesList;
	}
		
	public void printEdges() {
		DirectedEdge edge = edgeList.getHead();
		while (edge != null) {
			edge.print();
			edge = edge.getNext();
		}
	}
	
	public void printEndNodes() {
		DirectedEdge edge = edgeList.getHead();
		while (edge != null) {
			System.out.print(edge.getEnd().getKm1mer() + " -> ");
			edge = edge.getNext();
		}
	}
	
	public void printNode() {
		System.out.println(km1mer + " (" + indegree + ", " + outdegree + ")");
	}
	
	
}