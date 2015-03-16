package graph.debruijn;
public class DirectedEdge {
	private Node start, end;
	private String kmer;
	private boolean isVisited;
	private int weight;
	
	public DirectedEdge(Node start, Node end) {
		this.start = start;
		this.end = end;
		this.kmer = start.getKm1mer() + end.getKm1mer().substring(end.getKm1mer().length() - 1);
		this.weight = 1;
		this.isVisited = false;
	}
	
	public Node getStart() { return start; }
	public void setStart(Node start) { this.start = start; }
	
	public Node getEnd() { return end; }
	public void setEnd(Node end) { this.end = end; }
	
	public String getKmer() { return kmer; }
	public void setKmer(String kmer) { this.kmer = kmer; }
	
	public int getWeight() { return this.weight; }
	public void incrementWeight() { this.weight++; }
	public boolean decrementWeight() {
		if (this.weight>=1){
			this.weight--;
			if(this.weight == 0)
				this.isVisited = true;
			return true;
		}
		else
			return false;
	}
	
	public boolean isVisited() { return isVisited; }
	public void setVisited() { decrementWeight(); }
	
	public void print() { System.out.println(start.getKm1mer() + " >>" + kmer + ">> " + end.getKm1mer()); }
}