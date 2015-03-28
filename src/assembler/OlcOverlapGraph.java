package assembler;

import graph.overlap.*;
import interfaces.IGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class OlcOverlapGraph extends OverlapGraph implements IGraph {
	public OlcOverlapGraph(int minimumOverlapLength) {
		super(minimumOverlapLength);
	}
	
	public void constructGraph(File readsFile, int minimumOverlapLength) 
	{
		try (Scanner fileIn = new Scanner(readsFile)) {
			String currentLine = "";
			StringBuilder read = new StringBuilder();
			int readCount = 0;
			
			while (fileIn.hasNextLine()) {
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
			JOptionPane.showMessageDialog(null, "File " + readsFile.getAbsolutePath() + " not found.");
			System.err.println("File not found: " + readsFile);
		}
	}
	
	@SuppressWarnings("unchecked")
	public void traverseGraphToGenerateContigs(String outputFile) 
    {
		HashMap<Node, LinkedList<DirectedEdge>> adjacencyList;
		LinkedList<Node> contigNodeList;
		Node contigStartingNode, aNode;
		Iterator<Node> i;
		BufferedWriter writer;
		int contigCount;
		boolean newContigNodeAdded;
		try {
			writer = new BufferedWriter(new FileWriter(new File(outputFile)));
			contigNodeList = new LinkedList<Node>();
			adjacencyList = this.getAdjacencyList();
			contigCount = 0;
			i = adjacencyList.keySet().iterator();
			
			while (i.hasNext()) {
				contigStartingNode = i.next();
				
				if (!contigStartingNode.isVisited()) {
					contigNodeList.add(contigStartingNode);
					contigStartingNode.setVisited();
					newContigNodeAdded = true;
					while (!contigNodeList.isEmpty()) {
						aNode = getUnvisitedNeighbour(contigNodeList.getLast());
						if (aNode != null) {
							aNode.setVisited();
							contigNodeList.add(aNode);
							newContigNodeAdded = true;
						}
						else {
							if (newContigNodeAdded) {
								contigCount++;
								printContigInFastaFormat(writer, (LinkedList<Node>) contigNodeList.clone(), contigCount);
								newContigNodeAdded = false;
							}
							contigNodeList.removeLast();
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
			System.err.println("OlcOverlapGraph:generateContigs file "+outputFile+" cannot be created or opened");
		}
    }
}

