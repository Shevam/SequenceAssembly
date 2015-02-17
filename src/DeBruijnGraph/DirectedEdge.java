package DeBruijnGraph;
public class DirectedEdge {
	private Node start, end;
	private String kmer;
	private boolean isVisited;
	
	public DirectedEdge(Node start, Node end) {
		this.start = start;
		this.end = end;
		this.kmer = start.getKm1mer() + end.getKm1mer().substring(end.getKm1mer().length() - 1);
		this.isVisited = false;
	}
	
	public Node getStart() { return start; }
	public void setStart(Node start) { this.start = start; }
	
	public Node getEnd() { return end; }
	public void setEnd(Node end) { this.end = end; }
	
	public String getKmer() { return kmer; }
	public void setKmer(String kmer) { this.kmer = kmer; }
	
	public boolean isVisited() { return isVisited; }
	public void setVisited(boolean visited) { this.isVisited = visited; }
	
	public void print() { System.out.println(start.getKm1mer() + " >>" + kmer + ">> " + end.getKm1mer()); }
}