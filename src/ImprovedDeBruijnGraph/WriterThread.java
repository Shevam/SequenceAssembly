package improvedDeBruijnGraph;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WriterThread extends Thread
{
	private final static int fastaLineLength = 80;
	
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
		LinkedList<DirectedEdge> contigEdgeList;
		super.run();
		
		while(!listOfContigs.isEmpty())
		{
			contigEdgeList = listOfContigs.poll();
			if(contigEdgeList != null) {
				contigCount++;
				printContigInFastaFormat(writer, contigEdgeList, contigCount, kmerSize);
			}
		}
	}
	
	public void addContigToWriterBuffer(LinkedList<DirectedEdge> contigEdgeList)
	{
		try {
			listOfContigs.put(contigEdgeList);
		} catch (InterruptedException e) {
			System.err.println("WriterThread:addContigToWriterBuffer: interrupted while waiting for space contig list");
		} catch (ClassCastException e) {
			System.err.println("WriterThread:addContigToWriterBuffer: cannot add to contig list because of class conflict");
		} catch (NullPointerException e) {
			System.err.println("WriterThread:addContigToWriterBuffer: contigEdgeList is null");
		}
		
		this.run();
	}
	
	private void printContigInFastaFormat(BufferedWriter writer, LinkedList<DirectedEdge> contigEdgeList, int contigCount, int kmerSize) 
	{
		int writerRemainingLineSpace, counter;
		try {
			writer.write(">c" + contigCount + "_EdgeCount_"+ contigEdgeList.size() +"\n");
			writer.write(contigEdgeList.getFirst().getKmer());
			
			writerRemainingLineSpace = fastaLineLength - contigEdgeList.getFirst().getKmer().length();
			
			if (contigEdgeList.size()-1 > writerRemainingLineSpace) {
				for(int i=1; i<=writerRemainingLineSpace; i++)
					writer.write(contigEdgeList.get(i).getEnd().getKm1mer().charAt(kmerSize-2));
				writer.newLine();
				
				counter=0;
				for(int i=writerRemainingLineSpace+1; i<contigEdgeList.size(); i++)
				{
					writer.write(contigEdgeList.get(i).getEnd().getKm1mer().charAt(kmerSize-2));
					counter++;
					if(counter % fastaLineLength == 0)
						writer.newLine();
				}
			}
			else {
				for(int i=1; i<contigEdgeList.size(); i++)
					writer.write(contigEdgeList.get(i).getEnd().getKm1mer().charAt(kmerSize-2));
			}
			
			writer.flush();
			writer.write("\n");
		}
		catch (IOException e) {
			System.err.println("WriterThread:printContigInFastaFormat: error while writing to file");
		}
	}
	
	public int getContigCount()
	{
		return contigCount;
	}
}
