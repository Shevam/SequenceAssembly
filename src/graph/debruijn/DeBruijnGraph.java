package graph.debruijn;
import interfaces.IGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

public abstract class DeBruijnGraph implements IGraph {
	LinkedHashMap<String, Node> nodeList;
	HashMap<Node, LinkedList<DirectedEdge>> adjacencyList;
	int k;
	
	protected DeBruijnGraph() {
		nodeList = new LinkedHashMap<String, Node>();
		adjacencyList = new HashMap<Node, LinkedList<DirectedEdge>>();
	}
	
	protected Node addNode(String km1mer) {
		if (nodeList.containsKey(km1mer))
			return nodeList.get(km1mer);
		Node newNode = new Node(km1mer);
		nodeList.put(km1mer, newNode);
		return newNode;
	}
	
	protected DirectedEdge addEdge(String prefixString, String suffixString) {
		Node prefixNode, suffixNode;
		DirectedEdge newEdge;
		LinkedList<DirectedEdge> edgeList;
		
		prefixNode = nodeList.get(prefixString);
		if (prefixNode == null)
			prefixNode = addNode(prefixString);
		
		suffixNode = nodeList.get(suffixString);
		if (suffixNode == null)
			suffixNode = addNode(suffixString);
		
		edgeList = adjacencyList.get(prefixNode);
		
		if (edgeList == null) {
			adjacencyList.put(prefixNode, new LinkedList<DirectedEdge>());
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
		adjacencyList.get(prefixNode).add(newEdge);
		prefixNode.incrementOutdegree();
		suffixNode.incrementIndegree();
		
		return newEdge;
	}
	
	protected int getK() { return this.k; }
	protected void setK(int value) { this.k = value; }
	
	protected DirectedEdge getUnvisitedEdge() {
		for (Node node : nodeList.values()) {
			if (adjacencyList.get(node) != null) {
				for (DirectedEdge edge : adjacencyList.get(node)) {
					if (!edge.isVisited())
						return edge;
				}
			}
		}
		return null;
	}
	
	protected DirectedEdge getUnvisitedEdge(Node currentNode) {
		if (adjacencyList.get(currentNode) != null) {
			for (DirectedEdge edge : adjacencyList.get(currentNode)) {
				if (!edge.isVisited())
					return edge;
			}
		}
		return null;
	}
	
	protected DirectedEdge getZeroInDegreeUnvisitedEdge() {
		for (Node node : nodeList.values()) {
			if (adjacencyList.get(node) != null) {
				for (DirectedEdge edge : adjacencyList.get(node)) {
					if (!edge.isVisited() && (edge.getStart().getIndegree() == 0))
						return edge;
				}
			}
		}
		return null;
	}
	
	protected void breakReadIntoKmersAndAddToGraph(String read) 
	{
		int kmerSize = this.getK();
		for (int i = 0; i < read.length() - kmerSize + 1; i++)
			this.addEdge(read.substring(i, i + kmerSize - 1), read.substring(i + 1, i + kmerSize));
	}
	
	protected void printContigInFastaFormat(BufferedWriter writer, LinkedList<DirectedEdge> contigEdgeList, int contigCount, int kmerSize) 
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
			System.err.println("DebruijnGraph:printContigInFastaFormat: error while writing to file");
		}
	}
	
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
		for (Node node : nodeList.values()) {
			System.out.print(node.getKm1mer() + " --> ");
			if (adjacencyList.get(node) != null) {
				for (DirectedEdge edge : adjacencyList.get(node)) {
					System.out.print(edge.getEnd().getKm1mer() + ", ");
				}
			}
			System.out.println();
		}
		System.out.println();
	}
	
	public abstract void constructGraph(File readsFile, int kmerSize);
	public abstract void traverseGraphToGenerateContigs(String outputFile);
}

