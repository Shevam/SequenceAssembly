package assembler;

import interfaces.IGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import graph.debruijn.improved.*;

public class ImprovedDeBruijnGraph extends ImprovedDBG implements IGraph{
	public ImprovedDeBruijnGraph()
	{
		super();
	}
	
	public void constructGraph(File readsFile, int kmerSize) 
	{
		try (Scanner fileIn = new Scanner(readsFile)) {
			String currentLine = "";
			StringBuilder read = new StringBuilder();
			int readCount = 0;
			
			this.setKmerSize(kmerSize);
			System.out.println("kmer size: " + this.getKmerSize());
			
			while (fileIn.hasNextLine()) {
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
	
	public void traverseGraphToGenerateContigs(String generatedContigsFile)
	{
		DirectedEdge unvisitedEdge;
		Node node;
		BufferedWriter writer;
		ExecutorService es;
		boolean finished;
		int noOfConcurrentThreads;
		Iterator<Entry<String, Node>> iter;
		
		try {
			writer = new BufferedWriter(new FileWriter(new File(generatedContigsFile)));
			new WriterThread(writer, this.getKmerSize());
			noOfConcurrentThreads = 4;
			
			System.out.println("Number of concurrent threads: "+noOfConcurrentThreads);
			es = Executors.newFixedThreadPool(noOfConcurrentThreads);
			
			iter = nodeList.entrySet().iterator();
	        while(iter.hasNext()) {
	            node = iter.next().getValue();
	            if (node.getIndegree() == 0) {
	            	unvisitedEdge = node.getUnvisitedEdge();
	            	if (unvisitedEdge != null)
	            		es.execute(new TraversalThread(unvisitedEdge));
	            }
	        }
			
	        iter = nodeList.entrySet().iterator();
	        while(iter.hasNext()) {
	            node = iter.next().getValue();
	            unvisitedEdge = node.getUnvisitedEdge();
            	if (unvisitedEdge != null)
            		es.execute(new TraversalThread(unvisitedEdge));
	        }
	        
			es.shutdown();
			
			try {
				finished = es.awaitTermination(30, TimeUnit.MINUTES);
				if(!finished)
					System.err.println("ImprovedDeBruijnGraph:generateContigs: a traversal thread's timeout elapsed before finishing execution");
			} catch (InterruptedException e) {
				System.err.println("ImprovedDeBruijnGraph:generateContigs: thread executer interrupted while waiting termination of traversal thread");
			}
			
			System.out.println("Number of contigs generated: " + WriterThread.getInstance().getContigCount());
			writer.close();
		} catch (IOException e) {
			System.err.println("ImprovedDeBruijnGraph:generateContigs: file "+generatedContigsFile+" cannot be created or opened");
		}
	}
}
