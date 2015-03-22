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
	
	public WriterThread(BufferedWriter w, int k)
	{
		writer = w;
		kmerSize = k;
		contigCount = 0;
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
		LinkedList<DirectedEdge> contig;
		super.run();
		
		System.out.println("thread");
		
		while (true) {
			contig = writerQueue.poll();
			if(contig == null)
				break;
			
			contigCount++;
			printContigInFastaFormat(writer, contig, contigCount, kmerSize);
		}
	}
}

