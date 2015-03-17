package graph.debruijn.improved;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Node {

	private String km1mer;
	private int indegree, outdegree;
	public ConcurrentLinkedQueue<DirectedEdge> edgeList;
	
	public Node(String km1mer) {
		this.km1mer = km1mer;
		this.indegree = 0;
		this.outdegree = 0;
		this.edgeList = new ConcurrentLinkedQueue<DirectedEdge>();
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
	
	public DirectedEdge addEdgeTo(Node endNode) {
		boolean exists = false;
		DirectedEdge e, edge = new DirectedEdge(this, endNode);
		for (Iterator<DirectedEdge> iter = edgeList.iterator(); iter.hasNext();) {
			e = iter.next();
			if (endNode == e.getEnd()) {
				exists = true;
				edge = e;
				break;			
			}
		}
		if (exists) {
			edge.incrementWeight();
		} else {
			edgeList.add(edge);
		}
		edge.getStart().incrementOutdegree();
		edge.getEnd().incrementIndegree();
		return edge;
	}
	
	public DirectedEdge removeEdge(DirectedEdge edgeToRemove) {
		return edgeList.remove(edgeToRemove) ? edgeToRemove : null;
	}
	
	public void printNode() {
		System.out.println(km1mer + " (" + indegree + ", " + outdegree + ")");
	}
	
	public DirectedEdge getUnvisitedEdge() {
		DirectedEdge e;
		for (Iterator<DirectedEdge> iter = edgeList.iterator(); iter.hasNext();) {
			e = iter.next();
			if (e.getWeight()>0) {
				return e;
			}
		}
		return null;
	}
}