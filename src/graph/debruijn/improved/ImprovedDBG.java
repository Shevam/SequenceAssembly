package graph.debruijn.improved;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import mainPackage.GraphInterface;

public class ImprovedDBG implements GraphInterface {
	ConcurrentHashMap<String, Node> nodeList;
	int kmerSize;
	
	public ImprovedDBG() {
		super();
		nodeList = new ConcurrentHashMap<String, Node>();
	}
	
	public Node addNode(String km1mer) {
		if (nodeList.containsKey(km1mer))
			return nodeList.get(km1mer);
		Node newNode = new Node(km1mer);
		nodeList.put(km1mer, newNode);
		return newNode;
	}

	public DirectedEdge addEdge(String prefixString, String suffixString) {
		Node prefixNode, suffixNode;

		prefixNode = nodeList.get(prefixString);
		if (prefixNode == null)
			prefixNode = addNode(prefixString);

		suffixNode = nodeList.get(suffixString);
		if (suffixNode == null)
			suffixNode = addNode(suffixString);

		return prefixNode.addEdgeTo(suffixNode);
	}
	
	public int getKmerSize() { return this.kmerSize; }
	public void setKmerSize(int value) { this.kmerSize = value; }
	
	public void displayNodes() {
		System.out.println("Nodes (indeg, outdeg): ");
		for (Node node : nodeList.values()) {
			System.out.println(node.getKm1mer() + " (" + node.getIndegree() + ", " + node.getOutdegree() + ")");
		}
		System.out.println();
	}
	
	public void displayEdges() {
		System.out.println("Edges: ");
		for (Node node : nodeList.values()) {
			for (DirectedEdge edge : node.edgeList) {
				edge.print();
			}
		}
		System.out.println();
	}
	
	public void displayGraph() {
		System.out.println("Graph adjacency List:");
		for (Node node : nodeList.values()) {
			System.out.print(node.getKm1mer() + " --> ");
			for (DirectedEdge edge : node.edgeList) {
				System.out.print(edge.getEnd().getKm1mer() + "[" + edge.getWeight() + "], ");
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public DirectedEdge getUnvisitedEdge() {
        Iterator<Entry<String, Node>> iter = nodeList.entrySet().iterator();
        Entry<String, Node> entry;
        DirectedEdge edge;
        while(iter.hasNext())
        {
            entry = iter.next();
            edge = entry.getValue().getUnvisitedEdge();
            if (edge != null) {
            	return edge;
            }
        }
		return null;
	}

	public DirectedEdge getUnvisitedEdgeFromZeroIndegreeNode() {
		Iterator<Entry<String, Node>> iter = nodeList.entrySet().iterator();
        Node node;
        DirectedEdge edge;
        while(iter.hasNext())
        {
            node = iter.next().getValue();
            if (node.getIndegree() == 0) {
            	edge = node.getUnvisitedEdge();
            	if (edge != null) {
            		return edge;
            	}
            }
        }
		return null;
	}
	    
    public boolean isEulerian() {
    	@SuppressWarnings("unused")
		int nbal = 0, nsemi = 0, nneither = 0;
    	for (Node node : nodeList.values()) {
    		if (node.isBalanced())
    			nbal++;
    		else if (node.isSemiBalanced())
    			nsemi++;
    		else
    			nneither++;
    	}
    	if (nneither == 0 && nsemi == 2) // path
    		return true;
    	if (nneither == 0 && nsemi == 0) //cycle
    		return true;
    	return false;
    }
    
	public void constructGraph(File readsFile, int kmerSize) 
	{
		try (Scanner fileIn = new Scanner(readsFile)) 
		{
			String currentLine = "";
			StringBuilder read = new StringBuilder();
			int readCount = 0;
			
			this.setKmerSize(kmerSize);
			System.out.println("kmer size: " + this.getKmerSize());
			
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
	
	public void addKmersToGraph(String read) 
	{
		int kmerSize = this.getKmerSize();
		for (int i = 0; i < read.length() - kmerSize + 1; i++)
			this.addEdge(read.substring(i, i + kmerSize - 1), read.substring(i + 1, i + kmerSize));
	}
	
	public void generateContigs(String generatedContigsFile)
	{
		DirectedEdge unvisitedEdge;
		BufferedWriter writer;
		TraversalThread traversalThread;
		ExecutorService es;
		boolean finished;
		BlockingQueue<LinkedList<DirectedEdge>> queue = new LinkedBlockingQueue<LinkedList<DirectedEdge>>();
		try
		{
			writer = new BufferedWriter(new FileWriter(new File(generatedContigsFile)));
			new WriterThread(writer, this.getKmerSize(), queue);
			
			es = Executors.newFixedThreadPool(8);
			while (true)
			{			
				unvisitedEdge = this.getUnvisitedEdgeFromZeroIndegreeNode();
				if(unvisitedEdge==null)
					break;
				
				es.execute(traversalThread = new TraversalThread(unvisitedEdge, queue));
			}
			es.shutdown();
			
			try {
				finished = es.awaitTermination(30, TimeUnit.MINUTES);
				if(!finished)
					System.err.println("ImprovedDBG:generateContigs: a traversal thread's timeout elapsed before finishing execution");
			} catch (InterruptedException e) {
				System.err.println("ImprovedDBG:generateContigs: thread executer interrupted while waiting termination of traversal thread");
			}
			
			unvisitedEdge = this.getUnvisitedEdge();
			while(unvisitedEdge!=null)
			{
				traversalThread = new TraversalThread(unvisitedEdge, queue);
				
				try {
					traversalThread.start();
					traversalThread.join();
				} catch (IllegalThreadStateException e) {
					System.err.println("ImprovedDBG:generateContigs: traversal thread was already started");
				} catch (InterruptedException e) {
					System.err.println("ImprovedDBG:generateContigs: traversal thread join interrupted by another thread");
				}
				
				unvisitedEdge = this.getUnvisitedEdge();
			}
			System.out.println("Number of contigs generated: " + WriterThread.getInstance().getContigCount());
			writer.close();
		} catch (IOException e) {
			System.err.println("ImprovedDBG:generateContigs: file "+generatedContigsFile+" cannot be created or opened");
		}
	}
}
