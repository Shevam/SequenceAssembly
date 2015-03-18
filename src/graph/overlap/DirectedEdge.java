package graph.overlap;
public class DirectedEdge {
	private Node startNode, endNode;
	private String suffixToPrefix;
	private int overlapLength;
	
	public DirectedEdge(Node start, Node end, String suffixToPrefix) {
		start.incrementOutdegree();
		end.incrementIndegree();
		this.startNode = start;
		this.endNode = end;
		this.suffixToPrefix = suffixToPrefix;
		this.setOverlapLength(suffixToPrefix.length());
	}
	
	public Node getStart() { return startNode; }
	public void setStart(Node start) { this.startNode = start; }
	
	public Node getEnd() { return endNode; }
	public void setEnd(Node end) { this.endNode = end; }
	
	public String getsuffixToPrefix() { return suffixToPrefix; }
	public void setsuffixToPrefix(String suffixToPrefix) { this.suffixToPrefix = suffixToPrefix; }
	
	public void print() { System.out.println(startNode.getRead() + " >>" + suffixToPrefix + ">> " + endNode.getRead()); }

	public int getOverlapLength() {	return overlapLength; }
	public void setOverlapLength(int overlapLength) { this.overlapLength = overlapLength; }
}