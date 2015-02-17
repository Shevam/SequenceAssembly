package ImprovedDeBruijnGraph;
import java.io.BufferedWriter;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WriterThread extends Thread
{
	static BufferedWriter writer;
	static int kmerSize;
	private final BlockingQueue<LinkedList<DirectedEdge>> listOfContigs;
	private static final WriterThread instance = new WriterThread(writer, kmerSize);
	private int contigCount;
	
	WriterThread(BufferedWriter w, int k)
	{
		writer = w;
		kmerSize = k;
		listOfContigs = new LinkedBlockingQueue<LinkedList<DirectedEdge>>();
		contigCount = 0;
	}
	
	public static synchronized WriterThread getInstance()
	{
	    return instance;
	}
	
	public void run() 
	{
		super.run();
		while(!listOfContigs.isEmpty())
			writeToFile(listOfContigs.poll());
	}
	
	public void printContig(LinkedList<DirectedEdge> contigEdgeList)
	{
		try {
			listOfContigs.put(contigEdgeList);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		this.run();
	}
	
	public void writeToFile(LinkedList<DirectedEdge> contigEdgeList) {
    	if(contigEdgeList != null)
		{
    		contigCount++;
			try
			{
				writer.write(">c" + contigCount + "_EdgeCount_"+ contigEdgeList.size() +"\n");
				writer.write(contigEdgeList.getFirst().getKmer());
				for(int i=1; i<contigEdgeList.size(); i++)
					writer.write(contigEdgeList.get(i).getEnd().getKm1mer().charAt(kmerSize-2));
				writer.write("\n");
				writer.flush();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
    }
}
