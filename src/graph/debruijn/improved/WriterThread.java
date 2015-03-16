package graph.debruijn.improved;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.BlockingQueue;

public class WriterThread extends Thread
{
	
	static BufferedWriter writer;
	static int kmerSize;
	private static WriterThread instance = null;
	private int contigCount;
	private final BlockingQueue<LinkedList<DirectedEdge>> queue;
	
	WriterThread(BufferedWriter w, int k, BlockingQueue<LinkedList<DirectedEdge>> q)
	{
		writer = w;
		kmerSize = k;
		contigCount = 0;
		queue = q;
		instance = this;
	}
	
	public static synchronized WriterThread getInstance()
	{
	    return instance;
	}
	
	public void run() 
	{
		super.run();
		
		try {
			while (true) {
				LinkedList<DirectedEdge> contig = queue.take();
				if(contig != null) {
					contigCount++;
					printContigInFastaFormat(writer, contig, contigCount, kmerSize);
				}
			}
		} catch (InterruptedException ex) {
			ex.printStackTrace();
		}
	}
	
	private void printContigInFastaFormat(BufferedWriter writer, LinkedList<DirectedEdge> contig, int contigCount, int kmerSize) 
	{
		try {
			if (!contig.isEmpty()) {
				writer.write(">c" + contigCount + "_EdgeCount_"+ contig.size() +"\n");
				writer.write(contig.pollFirst().getKmer());
				while (!contig.isEmpty()) {
					writer.write(contig.pollFirst().getKmer().charAt(kmerSize-1));
				}
				writer.newLine();
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
