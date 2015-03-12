package improvedDeBruijnGraph;
import java.util.LinkedList;

public class TraversalThread extends Thread
{
	DirectedEdge startingEdge;
	LinkedList<DirectedEdge> contigEdgeList;
	WriterThread writerThread;
	public TraversalThread(DirectedEdge unvisitedEdge)
	{
		startingEdge=unvisitedEdge;
		contigEdgeList = new LinkedList<DirectedEdge>();
	}
	
	@SuppressWarnings("unchecked")
	public void run() 
	{
		Node endNode, lockedEndNode;
		DirectedEdge unvisitedEdge;
		boolean newContigEdgeAdded;
		super.run();
		
		contigEdgeList.add(startingEdge);
		startingEdge.setVisited();
		
		newContigEdgeAdded = true;
		while (!contigEdgeList.isEmpty()) {
			unvisitedEdge = contigEdgeList.getLast();
			
			endNode = unvisitedEdge.getEnd();
			do
				lockedEndNode = endNode.fetchNode();
			while(lockedEndNode == null);
			unvisitedEdge = lockedEndNode.getNextUnvisitedEdge();
			lockedEndNode.releaseNode();
			
			if (unvisitedEdge!=null) {
				unvisitedEdge.setVisited();
				contigEdgeList.add(unvisitedEdge);
				newContigEdgeAdded = true;
			}
			else {
				if(newContigEdgeAdded) {
					do
						writerThread = WriterThread.getInstance();
					while(writerThread == null);
					writerThread.addContigToWriterBuffer((LinkedList<DirectedEdge>) contigEdgeList.clone());
					newContigEdgeAdded = false;
				}
				contigEdgeList.removeLast();
			}
		}
	}
}
