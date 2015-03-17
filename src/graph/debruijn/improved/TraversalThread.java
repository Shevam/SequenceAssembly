package graph.debruijn.improved;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

public class TraversalThread extends Thread
{
	DirectedEdge startingEdge;
	LinkedList<DirectedEdge> path;
	WriterThread writerThread;
	private final BlockingQueue<LinkedList<DirectedEdge>> queue;
	
	public TraversalThread(DirectedEdge unvisitedEdge, BlockingQueue<LinkedList<DirectedEdge>> q)
	{
		startingEdge=unvisitedEdge;
		path = new LinkedList<DirectedEdge>();
		queue = q;
	}
	
	public void run() 
	{
		super.run();
		Node endNode;
		DirectedEdge unvisitedEdge;
		boolean edgeAdded;
		
		path.add(startingEdge);
		startingEdge.setVisited();
		
		edgeAdded = true;
		while (!path.isEmpty()) {
			unvisitedEdge = path.getLast();
			
			endNode = unvisitedEdge.getEnd();
			unvisitedEdge = endNode.getUnvisitedEdge();
			
			if (unvisitedEdge!=null) {
				unvisitedEdge.setVisited();
				path.add(unvisitedEdge);
				edgeAdded = true;
			}
			else {
				if(edgeAdded) {
					try {
						queue.put(new LinkedList<DirectedEdge>(path));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					edgeAdded = false;
				}
				path.removeLast();
			}
		}
	}
}
