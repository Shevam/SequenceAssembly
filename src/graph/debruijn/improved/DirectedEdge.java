package graph.debruijn.improved;
public class DirectedEdge {
	private Node start, end;
	private char value;
	private int weight;
	private boolean isVisited;
	
	public DirectedEdge(Node start, Node end) {
		this.start = start;
		this.end = end;
		this.value = end.getKm1mer().charAt(end.getKm1mer().length()-1);
		this.weight = 1;
		this.isVisited = false;
	}
	
	public synchronized Node getStart() { return start; }
	public void setStart(Node start) { this.start = start; }
	
	public synchronized Node getEnd() { return end; }
	public void setEnd(Node end) { this.end = end; }
	
	public char getValue() { return value; }
	public void setValue(char value) { this.value = value; }
	
	public int getWeight() { return this.weight; }
	public void incrementWeight() { this.weight++; }
	public void decrementWeight() {
		if (this.weight>0) {
			this.weight--;
		}
		else
			this.isVisited = true;
	}
	
	public boolean isVisited() { return isVisited; }
	public void setVisited() { decrementWeight(); }
	
	public void print() { System.out.println(start.getKm1mer() + " >>" + value + ">> " + end.getKm1mer()); }
}
