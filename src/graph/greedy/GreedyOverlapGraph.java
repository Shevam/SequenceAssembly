package graph.greedy;

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

public class GreedyOverlapGraph implements GraphInterface {
	private static int minimumOverlapLength;
	private LinkedHashMap<String, Node> nodeList;
	private HashMap<String, LinkedList<DirectedEdge>> adjacencyList;
	private Node leastIndegreeNode;
	private int fastaLineLength = 80;
	
	public GreedyOverlapGraph(int minimumOverlapLength) {
		GreedyOverlapGraph.minimumOverlapLength = minimumOverlapLength;
        nodeList = new LinkedHashMap<String, Node>();
        adjacencyList = new HashMap<String, LinkedList<DirectedEdge>>();
    }
	
	public Node addNode(String value) {
		if (nodeList.containsKey(value))
			return nodeList.get(value);
	    Node newNode = new Node(value);
	    nodeList.put(value, newNode);
	    
	    for(Node node : nodeList.values())
	    	checkForOverlapsAndAddEdges(node, newNode);
	    
	    if(nodeList.size() == 1)
	    	leastIndegreeNode = newNode;
	    else
	    	if(newNode.getIndegree()<leastIndegreeNode.getIndegree())
	    		leastIndegreeNode = newNode;
	    
	    return newNode;
	}
	
	private void checkForOverlapsAndAddEdges(Node node, Node newNode) {
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
			adjacencyList.put(from.getRead(), new LinkedList<DirectedEdge>());
			edgeList = adjacencyList.get(from.getRead());
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
	
	public Node getLeastIndegreeUnvisitedNode()
	{
		if(!leastIndegreeNode.isVisited())
			return leastIndegreeNode;
		else
		{
			Node leastIndegreeUnvisitedNode = null;
			for (Node node : nodeList.values()) {
				if(node.isVisited())
					continue;
				
				if (leastIndegreeUnvisitedNode == null)
					leastIndegreeUnvisitedNode = node;
				else
					if(node.getIndegree() < leastIndegreeUnvisitedNode.getIndegree())
						leastIndegreeUnvisitedNode = node;
			}
			return leastIndegreeUnvisitedNode;
		}
	}

	public Node getNextNodeWithHighestOverlapLength(Node currentNode) {
		
		DirectedEdge edgeWithHighestOverlapLength = null;
		if(adjacencyList.get(currentNode.getRead()) == null)
			return null;
		
		for (DirectedEdge edge : adjacencyList.get(currentNode.getRead())) {
			if(edge.getEnd().isVisited())
				continue;
			
			if(edgeWithHighestOverlapLength == null)
				edgeWithHighestOverlapLength = edge;
			else
				if(edge.getOverlapLength()>edgeWithHighestOverlapLength.getOverlapLength())
					edgeWithHighestOverlapLength = edge;
		}
		
		if(edgeWithHighestOverlapLength == null)
			return null;
		else
			return edgeWithHighestOverlapLength.getEnd();
	}

	public int getOverlapLength(Node node1, Node node2) {
		
		for (DirectedEdge edge : adjacencyList.get(node1.getRead())) {
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
	
	public String getSuperString(String startString, String endString)
    {
        String result = startString;
 
        int endIndex = endString.length() - 1;
        while(endIndex > 0 && !startString.endsWith(endString.substring(0, endIndex)))
            endIndex--;
 
        if(endIndex > 0)
            result += endString.substring(endIndex);
        else
            result += endString;
 
        return result;
    }
	
	public void constructGraph(File readsFile, int minimumOverlapLength) 
	{
		try (Scanner fileIn = new Scanner(readsFile)) 
		{
			String currentLine = "";
			StringBuilder read = new StringBuilder();
			int readCount = 0;
			new GreedyOverlapGraph(minimumOverlapLength);
			
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
	
	private void printContigInFastaFormat(BufferedWriter writer, LinkedList<Node> contigNodeList, int contigCount)
	{
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
			System.err.println("GreedyStaticMethods:printContigInFastaFormat: error while writing to file");
		}
	}

	public void generateContigs(String outputFile) 
    {
		BufferedWriter writer;
		LinkedList<Node> contigNodeList;
		Node currentNode;
		int contigCount = 0;
		
		try
		{
			writer = new BufferedWriter(new FileWriter(new File(outputFile)));
			while (true)
			{
				contigNodeList = new LinkedList<Node>();
				currentNode = this.getLeastIndegreeUnvisitedNode();
				if(currentNode == null)
					break;
				
		        while(currentNode != null)
		        {
		        	contigNodeList.add(currentNode);
			        currentNode.setVisited(true);
			        currentNode = this.getNextNodeWithHighestOverlapLength(currentNode);
		        }
		        
		        contigCount++;
		        printContigInFastaFormat(writer, contigNodeList, contigCount);
			}
			System.out.println("Number of contigs generated: " + contigCount);
			writer.close();
		}
		catch (FileNotFoundException e) {
			System.err.println("File not found: " + outputFile);
		} catch (IOException e) {
			System.err.println("GreedyStaticMethods:generateContigs file "+outputFile+" cannot be created or opened");
		}
    }
}
