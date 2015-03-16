package graph.debruijn;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;

import mainPackage.GraphInterface;

public class DeBruijnGraph implements GraphInterface {
	LinkedHashMap<String, Node> nodeList;
	HashMap<String, LinkedList<DirectedEdge>> adjacencyList;
	int k;
	int fastaLineLength = 80;
	
	public DeBruijnGraph() {
		super();
		nodeList = new LinkedHashMap<String, Node>();
		adjacencyList = new HashMap<String, LinkedList<DirectedEdge>>();
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
		DirectedEdge newEdge;
		LinkedList<DirectedEdge> edgeList;

		prefixNode = nodeList.get(prefixString);
		if (prefixNode == null)
			prefixNode = addNode(prefixString);
		
		suffixNode = nodeList.get(suffixString);
		if (suffixNode == null)
			suffixNode = addNode(suffixString);
		
		edgeList = adjacencyList.get(prefixString);
		
		if (edgeList == null) {
			adjacencyList.put(prefixString, new LinkedList<DirectedEdge>());
			edgeList = adjacencyList.get(prefixString);
		}
		else {
			for (DirectedEdge edge : edgeList) {
				if(edge.getEnd()==suffixNode){
					edge.incrementWeight();
					return edge;
				}
			}
		}
		
		newEdge = new DirectedEdge(prefixNode, suffixNode);
		edgeList.add(newEdge);
		prefixNode.incrementOutdegree();
		suffixNode.incrementIndegree();
		
		return newEdge;
	}
	
	public int getK() { return this.k; }
	public void setK(int value) { this.k = value; }
	
	public void displayNodes() {
		System.out.println("Nodes (indeg, outdeg): ");
		for (Node node : nodeList.values()) {
			System.out.println(node.getKm1mer() + " (" + node.getIndegree() + ", " + node.getOutdegree() + ")");
		}
		System.out.println();
	}
	
	public void displayEdges() {
		System.out.println("Edges: ");
		for (String node : nodeList.keySet()) {
			if (adjacencyList.get(node) != null) {
				for (DirectedEdge edge : adjacencyList.get(node)) {
					edge.print();
				}
			}
		}
		System.out.println();
	}
	
	public void displayGraph() {
		System.out.println("Graph adjacency List:");
		for (String node : nodeList.keySet()) {
			System.out.print(node + " --> ");
			if (adjacencyList.get(node) != null) {
				for (DirectedEdge edge : adjacencyList.get(node)) {
					System.out.print(edge.getEnd().getKm1mer() + ", ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public DirectedEdge getUnvisitedEdge() {
		for (String nodeValue : nodeList.keySet()) {
			if (adjacencyList.get(nodeValue) != null) {
				for (DirectedEdge edge : adjacencyList.get(nodeValue)) {
					if (!edge.isVisited())
						return edge;
				}
			}
		}
		return null;
	}
	
	public synchronized DirectedEdge getUnvisitedEdge(Node currentNode) {
		if (adjacencyList.get(currentNode.getKm1mer()) != null) {
			for (DirectedEdge edge : adjacencyList.get(currentNode.getKm1mer())) {
				if (!edge.isVisited())
					return edge;
			}
		}
		return null;
	}

	public DirectedEdge getZeroInDegreeUnvisitedEdge() { // TODO: should find only zero-indegree node?
		for (String nodeValue : nodeList.keySet()) {
			if (adjacencyList.get(nodeValue) != null) {
				for (DirectedEdge edge : adjacencyList.get(nodeValue)) {
					if (!edge.isVisited() && (edge.getStart().getIndegree() == 0))
						return edge;
				}
			}
		}
		return null;
	}
	
	public void constructGraph(File readsFile, int kmerSize) 
	{
		try (Scanner fileIn = new Scanner(readsFile)) 
		{
			String currentLine = "";
			StringBuilder read = new StringBuilder();
			int readCount = 0;
			
			new DeBruijnGraph();
			this.setK(kmerSize);
			System.out.println("kmer size: " + this.getK());
			
			while (fileIn.hasNextLine())
			{
				currentLine = fileIn.nextLine();
				
				if (currentLine.startsWith(">")) {
					if (!read.toString().equals("")) {
						breakReadIntoKmersAndAddToGraph(read.toString().toUpperCase());
						readCount++;
					}
					read = new StringBuilder();
				} 
				else
					read.append(currentLine.trim());
			}
			
			if (!read.toString().equals("")) {
				breakReadIntoKmersAndAddToGraph(read.toString().toUpperCase());
				readCount++;
			}
			
			System.out.println("Number of reads processed: " + readCount);
		}
		catch (FileNotFoundException e) {
			System.err.println("File not found: " + readsFile);
		}
	}

	public void breakReadIntoKmersAndAddToGraph(String read) 
	{
		int kmerSize = this.getK();
		for (int i = 0; i < read.length() - kmerSize + 1; i++)
			this.addEdge(read.substring(i, i + kmerSize - 1), read.substring(i + 1, i + kmerSize));
	}

	public void printContigInFastaFormat(BufferedWriter writer, LinkedList<DirectedEdge> contigEdgeList, int contigCount, int kmerSize) 
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
			System.err.println("DebruijnGraphStaticMethods:printContigInFastaFormat: error while writing to file");
		}
	}

	public void generateContigs(String outputFile)
	{
		LinkedList<DirectedEdge> contigEdgeList = new LinkedList<DirectedEdge>();
		DirectedEdge unvisitedEdge;
		BufferedWriter writer;
		int contigCount = 0;
		boolean newContigEdgeAdded;
		try
		{
			writer = new BufferedWriter(new FileWriter(new File(outputFile)));
			
			while (true)
			{
				unvisitedEdge = this.getZeroInDegreeUnvisitedEdge();
				if(unvisitedEdge==null)
				{
					unvisitedEdge = this.getUnvisitedEdge();
					if(unvisitedEdge==null)
						break;
				}
				else
				{
					contigEdgeList.add(unvisitedEdge);
					unvisitedEdge.setVisited();
					newContigEdgeAdded = true;
					while (!contigEdgeList.isEmpty()) {
						unvisitedEdge = this.getUnvisitedEdge(contigEdgeList.getLast().getEnd());
						if (unvisitedEdge!=null) {
							unvisitedEdge.setVisited();
							contigEdgeList.add(unvisitedEdge);
							newContigEdgeAdded = true;
						}
						else {
							if(newContigEdgeAdded) {
								contigCount++;
								printContigInFastaFormat(writer,contigEdgeList, contigCount, this.getK());
								newContigEdgeAdded = false;
							}
							contigEdgeList.removeLast();
						}
					}
				}
			}
			writer.close();
			System.out.println("Number of contigs generated: "+contigCount);
		}
		catch (FileNotFoundException e) {
			System.err.println("File not found: " + outputFile);
		} catch (IOException e) {
			System.err.println("DebruijnGraphStaticMethods:generateContigs file "+outputFile+" cannot be created or opened");
		}
	}

}
