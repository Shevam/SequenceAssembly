package assembler;

import graph.debruijn.*;
import interfaces.IGraph;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class TypicalDeBruijnGraph extends DeBruijnGraph implements IGraph{
	public TypicalDeBruijnGraph()
	{
		super();
	}
	
	public void constructGraph(File readsFile, int k) 
	{
		try (Scanner fileIn = new Scanner(readsFile)) {
			String currentLine = "";
			StringBuilder read = new StringBuilder();
			int readCount = 0;
			
			this.setK(k);
			System.out.println("k-mer size: " + this.getK());
			
			while (fileIn.hasNextLine()) {
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
	
	public void traverseGraphToGenerateContigs(String outputFile)
	{
		LinkedList<DirectedEdge> contigEdgeList = new LinkedList<DirectedEdge>();
		DirectedEdge unvisitedEdge;
		BufferedWriter writer;
		int contigCount = 0;
		boolean newContigEdgeAdded;
		try {
			writer = new BufferedWriter(new FileWriter(new File(outputFile)));
			
			while (true) {
				unvisitedEdge = this.getZeroInDegreeUnvisitedEdge();
				if(unvisitedEdge==null) {
					unvisitedEdge = this.getUnvisitedEdge();
					if(unvisitedEdge==null)
						break;
				}
				else {
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
			System.err.println("TypicalDebruijnGraph:generateContigs file "+outputFile+" cannot be created or opened");
		}
	}
}