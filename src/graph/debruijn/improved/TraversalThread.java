package graph.debruijn.improved;
import java.util.LinkedList;
import java.util.Stack;

public class TraversalThread extends Thread
{
	DirectedEdge startingEdge;
	LinkedList<DirectedEdge> contigEdgeList;
	
	public TraversalThread(DirectedEdge unvisitedEdge)
	{
		startingEdge=unvisitedEdge;
		contigEdgeList = new LinkedList<DirectedEdge>();
	}
	
	@SuppressWarnings("unchecked")
	public void run() 
	{
		super.run();
		Node endNode;
		DirectedEdge unvisitedEdge;
		boolean edgeAdded;
		DirectedEdge justVisitedEdge = null;
		Stack<DirectedEdge> backTraversalEdgeList;
		
		startingEdge.setVisited();
		
		backTraversalEdgeList = backTraverseGraph(startingEdge);
		while(!backTraversalEdgeList.isEmpty())
			contigEdgeList.add(backTraversalEdgeList.pop());
		
		contigEdgeList.add(startingEdge);
		
		edgeAdded = true;
		while (!contigEdgeList.isEmpty()) {
			unvisitedEdge = contigEdgeList.getLast();
			
			endNode = unvisitedEdge.getEnd();
			unvisitedEdge = endNode.getUnvisitedOutgoingEdge(justVisitedEdge);
			
			if (unvisitedEdge!=null) {
				unvisitedEdge.setVisited();
				contigEdgeList.add(unvisitedEdge);
				edgeAdded = true;
			}
			else {
				if(edgeAdded) {
					WriterThread.getInstance().addContigToWriterQueue((LinkedList<DirectedEdge>) contigEdgeList.clone());
					edgeAdded = false;
				}
				justVisitedEdge = contigEdgeList.removeLast();
			}
		}
	}

	private Stack<DirectedEdge> backTraverseGraph(DirectedEdge currentEdge) {
		Stack<DirectedEdge> backTraversalEdgeList = new Stack<DirectedEdge>();
		
		while (true) {
			currentEdge = currentEdge.getStart().getUnvisitedIncomingEdge();
			if(currentEdge != null) {
				currentEdge.setVisited();
				backTraversalEdgeList.push(currentEdge);
			}
			else
				return backTraversalEdgeList;
		}
	}
}