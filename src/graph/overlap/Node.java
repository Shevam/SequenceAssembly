package graph.overlap;

public class Node {

	private String read;
	private boolean isVisited;
	
	public Node(String read) {
		this.read = read;
		this.isVisited = false;
	}
	
	public String getRead() { return read; }
	public void setRead(String read) { this.read = read; }
	
	public boolean isVisited() { return isVisited; }
	public void setVisited() { this.isVisited = true; }
	
	public void printNodeInfo() {
		System.out.println("(" + read + ", " + isVisited + ")");
	}
}