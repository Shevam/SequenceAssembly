package graph.debruijn.improved;
import java.util.LinkedList;

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
		
		startingEdge.setVisited();
		contigEdgeList.add(startingEdge);
		
		edgeAdded = true;
		while (!contigEdgeList.isEmpty()) {
			unvisitedEdge = contigEdgeList.getLast();
			
			endNode = unvisitedEdge.getEnd();
			unvisitedEdge = endNode.getUnvisitedEdge(justVisitedEdge);
			
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
}
