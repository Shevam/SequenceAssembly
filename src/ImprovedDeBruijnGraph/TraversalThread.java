package ImprovedDeBruijnGraph;
import java.util.LinkedList;

public class TraversalThread extends Thread
{
	DirectedEdge edge;
	LinkedList<DirectedEdge> contigEdgeList;
	WriterThread writerThread;
	public TraversalThread(DirectedEdge unvisitedEdge)
	{
		edge=unvisitedEdge;
		contigEdgeList = new LinkedList<DirectedEdge>();
	}
	
	@SuppressWarnings("unchecked")
	public void run() 
	{
		super.run();
		Node aNode, lockedNode;
		
		try
		{
			contigEdgeList.add(edge);			
			aNode = edge.getEnd();
			
			do
				lockedNode = aNode.fetchNode();
			while(lockedNode == null);

			edge = lockedNode.getNextUnvisitedEdge();
			lockedNode.releaseNode();
			
			while (edge!=null)
			{
				edge.setVisited(true);
				contigEdgeList.add(edge);
				
				aNode = edge.getEnd();
				do
					lockedNode = aNode.fetchNode();
				while(lockedNode == null);
				edge = lockedNode.getNextUnvisitedEdge();
				lockedNode.releaseNode();
			}
			
			if(!contigEdgeList.isEmpty() && contigEdgeList != null)
			{
				do
					writerThread = WriterThread.getInstance();
				while(writerThread == null);
				
				writerThread.printContig((LinkedList<DirectedEdge>) contigEdgeList.clone());
			}
			
			while (!contigEdgeList.isEmpty())
			{
				edge = contigEdgeList.removeLast();
				
				aNode = edge.getStart();
				do
					lockedNode = aNode.fetchNode();
				while(lockedNode == null);
				
				edge = lockedNode.getNextUnvisitedEdge();
				lockedNode.releaseNode();
				
				if(edge!=null)
				{
					while (edge!=null)
					{
						edge.setVisited(true);
						contigEdgeList.add(edge);

						aNode = edge.getEnd();
						do
							lockedNode = aNode.fetchNode();
						while(lockedNode == null);
						edge = lockedNode.getNextUnvisitedEdge();
						lockedNode.releaseNode();
					}
					
					if(!contigEdgeList.isEmpty() && contigEdgeList != null)
					{
						do
							writerThread = WriterThread.getInstance();
						while(writerThread == null);
						
						writerThread.printContig((LinkedList<DirectedEdge>) contigEdgeList.clone());
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
