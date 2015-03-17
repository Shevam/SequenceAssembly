package graph.debruijn.improved;

public class DirectedEdge {
	private Node start, end;
	private String kmer;
	private int weight;
	
	public DirectedEdge(Node start, Node end) {
		this.start = start;
		this.end = end;
		this.kmer = start.getKm1mer() + end.getKm1mer().substring(end.getKm1mer().length() - 1);
		this.weight = 1;
	}
	
	public synchronized Node getStart() { return start; }
	public void setStart(Node start) { this.start = start; }
	
	public synchronized Node getEnd() { return end; }
	public void setEnd(Node end) { this.end = end; }
	
	public String getKmer() { return kmer; }
	public void setKmer(String kmer) { this.kmer = kmer; }
	
	public int getWeight() { return this.weight; }
	public void incrementWeight() { this.weight++; }
	public void decrementWeight() {
		if (this.weight>0) {
			this.weight--;
		}
	}
	
	public void setVisited() {
		decrementWeight();
//		if (weight == 0) {
//			this.getStart().removeEdge(this);
//		}
	}
	
	public void print() { System.out.println(start.getKm1mer() + " >>" + kmer + ">> " + end.getKm1mer()); }
}