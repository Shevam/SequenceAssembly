package graph.debruijn.improved;
import java.util.Comparator;
import java.util.concurrent.PriorityBlockingQueue;

public class Node {

	private String km1mer;
	private int indegree, outdegree;
	public PriorityBlockingQueue<DirectedEdge> edgeList;
	
	public Node(String km1mer) {
		this.km1mer = km1mer;
		this.indegree = 0;
		this.outdegree = 0;
		this.edgeList = new PriorityBlockingQueue<DirectedEdge>(11, new EdgeComparator());
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
		DirectedEdge edge = new DirectedEdge(this, endNode);
		for (DirectedEdge e : edgeList) {
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
//		if (this.edgeList.peek()!=null && this.edgeList.peek().getWeight() > 1) {
//			this.edgeList.peek().decrementWeight();
//			return this.edgeList.peek();
//		}
//		return this.edgeList.poll();
		if (edgeList.peek()!=null && edgeList.peek().getWeight()==0)
			System.err.println("HEAD WEIGHT 0!");
		return this.edgeList.peek();
	}
	
	class EdgeComparator implements Comparator<DirectedEdge> {
		@Override
		public int compare(DirectedEdge o1, DirectedEdge o2) {
			if (o1.getWeight() != 0) 
				return -1;
			if (o2.getWeight() != 0)
				return 1;
			return 0;
		}
	}
}