package graph.debruijn.improved;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class WriterThread extends Thread
{
	private static BufferedWriter writer;
	private static int kmerSize;
	private static WriterThread instance = null;
	private int contigCount;
	public final BlockingQueue<LinkedList<DirectedEdge>> writerQueue;
	public static int longestContig;
	
	public WriterThread(BufferedWriter w, int k)
	{
		writer = w;
		kmerSize = k;
		contigCount = 0;
		longestContig = 0;
		this.writerQueue = new LinkedBlockingQueue<LinkedList<DirectedEdge>>();
		instance = this;
		this.run();
	}
	
	public static synchronized WriterThread getInstance() { return instance; }
	
	public int getContigCount() { return contigCount; }
	
	protected synchronized void printContigInFastaFormat(BufferedWriter writer, LinkedList<DirectedEdge> contigEdgeList, int contigCount, int kmerSize) 
	{
		int writerRemainingLineSpace, counter, fastaLineLength;
		
		fastaLineLength = ImprovedDBG.fastaLineLength;
		try {
			writer.write(">c" + contigCount + "_EdgeCount_"+ contigEdgeList.size() +"\n");
			writer.write(contigEdgeList.getFirst().getStart().getKm1mer());
			
			writerRemainingLineSpace = fastaLineLength - contigEdgeList.getFirst().getStart().getKm1mer().length();
			
			if (contigEdgeList.size()-1 > writerRemainingLineSpace) {
				for(int i=1; i<=writerRemainingLineSpace; i++)
					writer.write(contigEdgeList.get(i).getValue());
				writer.newLine();
				
				counter=0;
				for(int i=writerRemainingLineSpace+1; i<contigEdgeList.size(); i++)
				{
					writer.write(contigEdgeList.get(i).getValue());
					counter++;
					if(counter % fastaLineLength == 0)
						writer.newLine();
				}
			}
			else {
				for(int i=1; i<contigEdgeList.size(); i++)
					writer.write(contigEdgeList.get(i).getValue());
			}
			
			writer.write("\n");
			writer.flush();
		}
		catch (IOException e) {
			System.err.println("DebruijnGraph:printContigInFastaFormat: error while writing to file");
		}
	}
	
	public void addContigToWriterQueue(LinkedList<DirectedEdge> contig)
	{
		try { this.writerQueue.put(contig);
		} catch (InterruptedException e) {
			System.err.println("writerThread interrupted while waiting to add to writerQueue");
		}
		this.run();
	}
	
	public void run() 
	{
		LinkedList<DirectedEdge> contigEdgeList;
		super.run();
		
		while (true) {
			contigEdgeList = writerQueue.poll();
			if(contigEdgeList == null)
				break;
			
			contigCount++;
			if (contigEdgeList.size()>1)
				printContigInFastaFormat(writer, contigEdgeList, contigCount, kmerSize);
			
			if (longestContig < contigEdgeList.size()+kmerSize-1)
				longestContig = contigEdgeList.size()+kmerSize-1;
		}
	}
}

