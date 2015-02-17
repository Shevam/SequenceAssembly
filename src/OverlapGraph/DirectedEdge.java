package OverlapGraph;
public class DirectedEdge {
	private Node start, end;
	private String suffixToPrefix;
	private int overlapLength;
	
	public DirectedEdge(Node start, Node end, String suffixToPrefix) {
		start.incrementOutdegree();
		end.incrementIndegree();
		this.start = start;
		this.end = end;
		this.suffixToPrefix = suffixToPrefix;
		this.setOverlapLength(suffixToPrefix.length());
	}
	
	public Node getStart() { return start; }
	public void setStart(Node start) { this.start = start; }
	
	public Node getEnd() { return end; }
	public void setEnd(Node end) { this.end = end; }
	
	public String getsuffixToPrefix() { return suffixToPrefix; }
	public void setsuffixToPrefix(String suffixToPrefix) { this.suffixToPrefix = suffixToPrefix; }
	
	public void print() { System.out.println(start.getRead() + " >>" + suffixToPrefix + ">> " + end.getRead()); }

	public int getOverlapLength() {	return overlapLength; }
	public void setOverlapLength(int overlapLength) { this.overlapLength = overlapLength; }
}