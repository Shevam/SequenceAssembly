package OverlapGraph;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class OverlapGraphStaticMethods
{
	private static String getSequence(String SEQUENCE_FILE)
	{
		try (Scanner fileIn = new Scanner(new File(SEQUENCE_FILE))) 
		{
			String currentLine = "";
			StringBuilder sequence = new StringBuilder();
			
			while (fileIn.hasNextLine())
			{
				currentLine = fileIn.nextLine();
				
				if (!currentLine.startsWith(">"))
					sequence.append(currentLine.trim());
			}
			return sequence.toString();
		}
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static void generateReads(String SEQUENCE_FILE, int READ_SIZE, int MINIMUM_OVERLAP_LENGTH,String OUTPUT_FILE)
	{
		String sequence = getSequence(SEQUENCE_FILE);
		BufferedWriter writer;
		if(sequence != null && !sequence.equals("") && sequence.length()>READ_SIZE)
		{
			try 
			{
				writer = new BufferedWriter(new FileWriter(new File(OUTPUT_FILE)));
				int sequenceSection=0, readCount=0;
				sequenceSection++;
				int aRandomNumber = new Random().nextInt(READ_SIZE - MINIMUM_OVERLAP_LENGTH);
				while(sequenceSection*READ_SIZE<sequence.length())
				{
					String circularRead;
					aRandomNumber = new Random().nextInt(READ_SIZE - MINIMUM_OVERLAP_LENGTH);
					
					if (((sequenceSection)*READ_SIZE)+aRandomNumber <= sequence.length()){
						readCount++;
						circularRead = sequence.substring(((sequenceSection-1)*READ_SIZE)+aRandomNumber, (sequenceSection*READ_SIZE)+aRandomNumber);
						writer.write(">r" + readCount + "\n");
						for(int i=0;i<circularRead.length();i+=80) {
							if(i+80 > circularRead.length())
								writer.write(circularRead.substring(i) + "\n");
							else
								writer.write(circularRead.substring(i, i+80) + "\n");
						}
						
						readCount++;
						circularRead = sequence.substring((sequenceSection-1)*READ_SIZE, sequenceSection*READ_SIZE);
						writer.write(">r" + readCount + "\n");
						for(int i=0;i<circularRead.length();i+=80) {
							if(i+80 > circularRead.length())
								writer.write(circularRead.substring(i) + "\n");
							else
								writer.write(circularRead.substring(i, i+80) + "\n");
						}
						sequenceSection++;
					}
					else {
						readCount++;
						circularRead = sequence.substring(((sequenceSection-1)*READ_SIZE)+aRandomNumber);
						int remainingNoOfChars = READ_SIZE - circularRead.length();
						circularRead += sequence.substring(0, remainingNoOfChars);
						writer.write(">r" + readCount + "\n");
						for(int i=0;i<circularRead.length();i+=80) {
							if(i+80 > circularRead.length())
								writer.write(circularRead.substring(i) + "\n");
							else
								writer.write(circularRead.substring(i, i+80) + "\n");
						}
						break;
					}
					writer.flush();
				}
				
				System.out.println("Number of reads generated: "+readCount);
				writer.close();
			}
			catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getOverlap(String startString, String endString, int minimumOverlapLength) {
		int endIndex = endString.length() - 1;
		while (endIndex >= minimumOverlapLength	&& !startString.endsWith(endString.substring(0, endIndex)))
			endIndex--;
		if(!startString.endsWith(endString.substring(0, endIndex)))
			return null;
		return endString.substring(0, endIndex);
	}
	
	private static String getSuperString(String startString, String endString)
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
	
	public static void constructGraph(File readsFile, int minimumOverlapLength) 
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
						OverlapGraph.getInstance().addNode(read.toString().toUpperCase());
						readCount++;
					}
					read = new StringBuilder();
				} 
				else
					read.append(currentLine.trim());
			}

			if (!read.toString().equals("")) {
				OverlapGraph.getInstance().addNode(read.toString().toUpperCase());
				readCount++;
			}

			System.out.println("Number of reads processed: " + readCount);
		}
		catch (FileNotFoundException e) {
			System.err.println("File not found: " + readsFile);
		}
	}
	
	public static void simplifyGraph() 
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
	
	private static Node getUnvisitedNeighbour(Node aNode) 
    {
		LinkedList<DirectedEdge> edgeList;
		
		edgeList = OverlapGraph.getInstance().getAdjacencyList(aNode);
		if(edgeList != null)
		{
			for (DirectedEdge edge : edgeList) {
				if(!edge.getEnd().isVisited())
					return edge.getEnd();
			}
		}
		return null;
    }
	
	private static void printContig(BufferedWriter writer, LinkedList<Node> contigNodeList, int contigCount) {
		
		try{
			int writerRemainingLineSpace = 0, lineLength = 80;
			String contigPart;
			
        	writer.write(">c" + contigCount + "_NodeCount_"+ contigNodeList.size() +"\n");
			contigPart = contigNodeList.getFirst().getRead();
			
			for(int i=0;i<contigPart.length();i+=lineLength) {
				if(i+lineLength > contigPart.length()) {
					writer.write(contigPart.substring(i));
					writerRemainingLineSpace = lineLength - (contigPart.length() - i);
				}
				else
					writer.write(contigPart.substring(i, i+lineLength) + "\n");
			}
			
			for(int j=1; j<contigNodeList.size(); j++)
			{
				contigPart = contigNodeList.get(j).getRead().substring(OverlapGraph.getInstance().getOverlapLength(contigNodeList.get(j-1), contigNodeList.get(j)));
				
				if(contigPart.length() > writerRemainingLineSpace) {
					writer.write(contigPart.substring(0, writerRemainingLineSpace) + "\n");
					for(int i=writerRemainingLineSpace;i<contigPart.length();i+=lineLength) {
						if(contigPart.length() < i+lineLength) {
							writer.write(contigPart.substring(i));
							writerRemainingLineSpace = lineLength - (contigPart.length() - i);
						}
						else
							writer.write(contigPart.substring(i, i+lineLength) + "\n");
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
			e.printStackTrace();
		}
	}
	
	@SuppressWarnings("unchecked")
	public static void generateContigs(String outputFile) 
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
			adjacencyList = OverlapGraph.getInstance().getAdjacencyList();
			i = adjacencyList.keySet().iterator();
			startingNode = i.next();
		    contigCount = 0;
		    
			nodeList.add(startingNode);
			while(!nodeList.isEmpty()) {
				aNode = getUnvisitedNeighbour(nodeList.getLast());
				
				if(aNode == null) {
					contigCount++;
					printContig(writer, (LinkedList<Node>) nodeList.clone(), contigCount);
					nodeList.removeLast();
				}
				else {
					aNode.setVisited(true);
					nodeList.add(aNode);
				}
			}
			
			System.out.println("Number of contigs generated: " + contigCount);
			writer.close();
		}
		catch (FileNotFoundException e) {
			System.err.println("File not found: " + outputFile);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
    }
}
