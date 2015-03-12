package improvedDeBruijnGraph;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ImprovedDBGStaticMethods
{
	public static void constructGraph(File readsFile, int kmerSize) 
	{
		try (Scanner fileIn = new Scanner(readsFile)) 
		{
			String currentLine = "";
			StringBuilder read = new StringBuilder();
			int readCount = 0;
			
			new DeBruijnGraph();
			DeBruijnGraph.getInstance().setKmerSize(kmerSize);
			System.out.println("kmer size: " + DeBruijnGraph.getInstance().getKmerSize());
			
			while (fileIn.hasNextLine())
			{
				currentLine = fileIn.nextLine();

				if (currentLine.startsWith(">")) {
					if (!read.toString().equals("")) {
						addKmersToGraph(read.toString().toUpperCase());
						readCount++;
					}
					read = new StringBuilder();
				} 
				else
					read.append(currentLine.trim());
			}

			if (!read.toString().equals("")) {
				addKmersToGraph(read.toString().toUpperCase());
				readCount++;
			}

			System.out.println("Number of reads processed: " + readCount);
		}
		catch (FileNotFoundException e) {
			System.err.println("File not found: " + readsFile);
		}
	}
	
	public static void addKmersToGraph(String read) 
	{
		int kmerSize = DeBruijnGraph.getInstance().getKmerSize();
		for (int i = 0; i < read.length() - kmerSize + 1; i++)
			DeBruijnGraph.getInstance().addEdge(read.substring(i, i + kmerSize - 1), read.substring(i + 1, i + kmerSize));
	}
	
	public static void generateContigs(String generatedContigsFile)
	{
		DirectedEdge unvisitedEdge;
		BufferedWriter writer;
		TraversalThread traversalThread;
		ExecutorService es;
		boolean finished;
		DeBruijnGraph graph;
		try
		{
			writer = new BufferedWriter(new FileWriter(new File(generatedContigsFile)));
			new WriterThread(writer, DeBruijnGraph.getInstance().getKmerSize());
			
			es = Executors.newFixedThreadPool(8);
			while (true)
			{
				do
					graph = DeBruijnGraph.getInstance();
				while(graph==null);
				
				unvisitedEdge = graph.getZeroInDegreeUnvisitedEdge();
				if(unvisitedEdge==null)
					break;
				
				es.execute(traversalThread = new TraversalThread(unvisitedEdge));
			}
			es.shutdown();
			
			try {
				finished = es.awaitTermination(30, TimeUnit.MINUTES);
				if(!finished)
					System.err.println("ImprovedDBGStaticMethods:generateContigs: a traversal thread's timeout elapsed before finishing execution");
			} catch (InterruptedException e) {
				System.err.println("ImprovedDBGStaticMethods:generateContigs: thread executer interrupted while waiting termination of traversal thread");
			}
			
			do
				graph = DeBruijnGraph.getInstance();
			while(graph==null);
			
			unvisitedEdge = graph.getUnvisitedEdge();
			while(unvisitedEdge!=null)
			{
				traversalThread = new TraversalThread(unvisitedEdge);
				
				try {
					traversalThread.start();
					traversalThread.join();
				} catch (IllegalThreadStateException e) {
					System.err.println("ImprovedDBGStaticMethods:generateContigs: traversal thread was already started");
				} catch (InterruptedException e) {
					System.err.println("ImprovedDBGStaticMethods:generateContigs: traversal thread join interrupted by another thread");
				}
				
				do
					graph = DeBruijnGraph.getInstance();
				while(graph==null);
				unvisitedEdge = graph.getUnvisitedEdge();
			}
			System.out.println("Number of contigs generated: " + WriterThread.getInstance().getContigCount());
			writer.close();
		} catch (IOException e) {
			System.err.println("ImprovedDBGStaticMethods:generateContigs: file "+generatedContigsFile+" cannot be created or opened");
		}
	}
}
