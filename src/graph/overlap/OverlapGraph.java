package graph.overlap;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Scanner;

import mainPackage.GraphInterface;

public class OverlapGraph implements GraphInterface {
	private static int minimumOverlapLength;
	private LinkedHashMap<String, Node> nodeList;
	private HashMap<Node, LinkedList<DirectedEdge>> adjacencyList;
	int fastaLineLength = 80;
	
	public OverlapGraph(int minimumOverlapLength) {
		OverlapGraph.minimumOverlapLength = minimumOverlapLength;
        nodeList = new LinkedHashMap<String, Node>();
        adjacencyList = new HashMap<Node, LinkedList<DirectedEdge>>();
    }
	
	public Node addNode(String value) {
		if (nodeList.containsKey(value))
			return nodeList.get(value);
	    Node newNode = new Node(value);
	    nodeList.put(value, newNode);
	    
	    for(Node node : nodeList.values())
	    	checkForOverlapsAndAddEdges(node, newNode);
	    
	    return newNode;
	}
	
	public void checkForOverlapsAndAddEdges(Node node, Node newNode) {
		String overlap;
		if(node.getRead().contains(newNode.getRead().substring(0, minimumOverlapLength-1)))
		{
			overlap = this.getOverlap(node.getRead(), newNode.getRead(), minimumOverlapLength);
			if(overlap != null)
				addEdge(node, newNode, overlap);
		}
		if(newNode.getRead().contains(node.getRead().substring(0, minimumOverlapLength-1)))
		{
			overlap = this.getOverlap(newNode.getRead(), node.getRead(), minimumOverlapLength);
			if(overlap != null)
				addEdge(newNode, node, overlap);
		}
	}

	public DirectedEdge addEdge(Node from, Node to, String suffixToPrefix) {
		LinkedList<DirectedEdge> edgeList;
		DirectedEdge newEdge;
		
        if (from == null || to == null || suffixToPrefix.equals(""))
        	return null;
        
        edgeList = adjacencyList.get(from);
        
        if (edgeList == null) {
			adjacencyList.put(from, new LinkedList<DirectedEdge>());
			edgeList = adjacencyList.get(from);
		}
        else {
        	for(DirectedEdge edge : edgeList)
            	if(edge.getEnd() == to)
            		return edge;
        }
        
    	newEdge = new DirectedEdge(from, to, suffixToPrefix);
        edgeList.add(newEdge);
        
        return newEdge;
	}
	
	public LinkedHashMap<String, Node> getNodeList() {
		return nodeList;
	}
	
	public LinkedList<DirectedEdge> getAdjacencyList(Node aNode) {
		return adjacencyList.get(aNode);
	}
	
	public HashMap<Node, LinkedList<DirectedEdge>> getAdjacencyList() {
		return adjacencyList;
	}
	
	public int getOverlapLength(Node node1, Node node2) {
		for (DirectedEdge edge : adjacencyList.get(node1)) {
			if(edge.getEnd() == node2)
				return edge.getOverlapLength();
		}
		return 0;
	}
	
	
	
	
	
	public String getOverlap(String startString, String endString, int minimumOverlapLength) {
		int endIndex = endString.length() - 1;
		while (endIndex >= minimumOverlapLength	&& !startString.endsWith(endString.substring(0, endIndex)))
			endIndex--;
		if(!startString.endsWith(endString.substring(0, endIndex)))
			return null;
		return endString.substring(0, endIndex);
	}
	
	public void constructGraph(File readsFile, int minimumOverlapLength) 
	{
		try (Scanner fileIn = new Scanner(readsFile)) 
		{
			String currentLine = "";
			StringBuilder read = new StringBuilder();
			int readCount = 0;
			new OverlapGraph(minimumOverlapLength);
			
			while (fileIn.hasNextLine())
			{
				currentLine = fileIn.nextLine();

				if (currentLine.startsWith(">")) {
					if (!read.toString().equals("")) {
						this.addNode(read.toString().toUpperCase());
						readCount++;
					}
					read = new StringBuilder();
				} 
				else
					read.append(currentLine.trim());
			}

			if (!read.toString().equals("")) {
				this.addNode(read.toString().toUpperCase());
				readCount++;
			}

			System.out.println("Number of reads processed: " + readCount);
		}
		catch (FileNotFoundException e) {
			System.err.println("File not found: " + readsFile);
		}
	}
	
	public void simplifyGraph() 
    {
		/*
        OverlapGraph g;
        Collection<Node> listOfNodes;
        String combinedRead;
        LinkedList<DirectedEdge> listOfEdges;
        DirectedEdge edgeToEliminate;
        
        g = OverlapGraph.getInstance().combineNodesLinkedBySingleEdge;
        listOfNodes = g.getNodeList();
        for(Node node : listOfNodes) {
        	if(node.getOutdegree() == 1) {
        		listOfEdges = g.getAdjacencyList(node.getRead());
        		if(listOfEdges.size() != 1)
        			System.err.println("Numbers of outgoing edges not equal outdegree of node!");
        		else {
        			edgeToEliminate = listOfEdges.remove();
        			combinedRead = node.getRead().concat(edgeToEliminate.getEnd().getRead());
        			node.setRead(combinedRead);
        			g.
        		}
        	}
        }*/
    }
	
	private Node getUnvisitedNeighbour(Node aNode) 
    {
		LinkedList<DirectedEdge> edgeList;
		
		edgeList = this.getAdjacencyList(aNode);
		if(edgeList != null)
		{
			for (DirectedEdge edge : edgeList) {
				if(!edge.getEnd().isVisited())
					return edge.getEnd();
			}
		}
		return null;
    }
	
	private void printContigInFastaFormat(BufferedWriter writer, LinkedList<Node> contigNodeList, int contigCount) {
		
		try{
			int writerRemainingLineSpace = 0;
			String contigPart;
			
        	writer.write(">c" + contigCount + "_NodeCount_"+ contigNodeList.size() +"\n");
			contigPart = contigNodeList.getFirst().getRead();
			
			for(int i=0;i<contigPart.length();i+=fastaLineLength) {
				if(i+fastaLineLength > contigPart.length()) {
					writer.write(contigPart.substring(i));
					writerRemainingLineSpace = fastaLineLength - (contigPart.length() - i);
				}
				else
					writer.write(contigPart.substring(i, i+fastaLineLength) + "\n");
			}
			
			for(int j=1; j<contigNodeList.size(); j++)
			{
				contigPart = contigNodeList.get(j).getRead().substring(this.getOverlapLength(contigNodeList.get(j-1), contigNodeList.get(j)));
				
				if(contigPart.length() > writerRemainingLineSpace) {
					writer.write(contigPart.substring(0, writerRemainingLineSpace) + "\n");
					for(int i=writerRemainingLineSpace;i<contigPart.length();i+=fastaLineLength) {
						if(contigPart.length() < i+fastaLineLength) {
							writer.write(contigPart.substring(i));
							writerRemainingLineSpace = fastaLineLength - (contigPart.length() - i);
						}
						else
							writer.write(contigPart.substring(i, i+fastaLineLength) + "\n");
					}
				}
				else {
					writer.write(contigPart);
					writerRemainingLineSpace -= contigPart.length();
				}
			}
			writer.flush();
			writer.newLine();
		}
		catch (IOException e) {
			System.err.println("OverlapGraphStaticMethods:printContigInFastaFormat: error while writing to file");
		}
	}
	
	public void generateContigs(String outputFile) 
    {
		HashMap<Node, LinkedList<DirectedEdge>> adjacencyList;
		LinkedList<Node> nodeList;
		Node startingNode, aNode;
		Iterator<Node> i;
		BufferedWriter writer;
		int contigCount;
		
		try {
			writer = new BufferedWriter(new FileWriter(new File(outputFile)));
			nodeList = new LinkedList<Node>();
			adjacencyList = this.getAdjacencyList();
			contigCount = 0;
			i = adjacencyList.keySet().iterator();

			while (i.hasNext()) {
				startingNode = i.next();

				if (!startingNode.isVisited()) {
					nodeList.add(startingNode);
					startingNode.setVisited();
					while (!nodeList.isEmpty()) {
						aNode = getUnvisitedNeighbour(nodeList.getLast());

						if (aNode == null) {
							contigCount++;
							printContigInFastaFormat(writer, (LinkedList<Node>) nodeList.clone(), contigCount);
							nodeList.removeLast();
						} else {
							aNode.setVisited();
							nodeList.add(aNode);
						}
					}
				}
			}
			
			System.out.println("Number of contigs generated: " + contigCount);
			writer.close();
		}
		catch (FileNotFoundException e) {
			System.err.println("File not found: " + outputFile);
		}
		catch (IOException e) {
			System.err.println("OverlapGraphStaticMethods:generateContigs file "+outputFile+" cannot be created or opened");
		}
    }
}
