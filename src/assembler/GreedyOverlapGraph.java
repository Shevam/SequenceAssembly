package assembler;

import graph.overlap.*;
import interfaces.IGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class GreedyOverlapGraph extends OverlapGraph implements IGraph{
	public GreedyOverlapGraph(int minimumOverlapLength) {
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
			JOptionPane.showMessageDialog(null, "File " + readsFile.getAbsolutePath() + " not found.");//will this cause a prob if run from console
			System.err.println("File not found: " + readsFile);
		}
	}
	
	public void traverseGraphToGenerateContigs(String outputFile) 
    {
		BufferedWriter writer;
		LinkedList<Node> contigNodeList;
		Node currentNode;
		int contigCount = 0;
		
		try {
			writer = new BufferedWriter(new FileWriter(new File(outputFile)));
			while (true) {
				contigNodeList = new LinkedList<Node>();
				currentNode = this.getLeastIndegreeUnvisitedNode();
				if(currentNode == null)
					break;
				
		        while(currentNode != null) {
		        	contigNodeList.add(currentNode);
			        currentNode.setVisited();
			        currentNode = this.getNextNodeWithHighestOverlapLength(currentNode);
		        }
		        contigCount++;
		        printContigInFastaFormat(writer, contigNodeList, contigCount);
			}
			System.out.println("Number of contigs generated: " + contigCount);
			System.err.println("Longest contig number: " + longestContigNo);
			writer.close();
		}
		catch (FileNotFoundException e) {
			System.err.println("File not found: " + outputFile);
		} catch (IOException e) {
			System.err.println("GreedyOverlapGraph:generateContigs file "+outputFile+" cannot be created or opened");
		}
    }
}

