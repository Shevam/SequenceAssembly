package deBruijnGraph;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class DebruijnGraphStaticMethods
{
	private final static int fastaLineLength = 80;
	
	public static void constructGraph(File readsFile, int kmerSize) 
	{
		try (Scanner fileIn = new Scanner(readsFile)) 
		{
			String currentLine = "";
			StringBuilder read = new StringBuilder();
			int readCount = 0;
			
			new DeBruijnGraph();
			DeBruijnGraph.getInstance().setKmerSize(kmerSize);
			System.out.println("kmer size: " + DeBruijnGraph.getInstance().getKmerSize());
			
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
	
	public static void breakReadIntoKmersAndAddToGraph(String read) 
	{
		int kmerSize = DeBruijnGraph.getInstance().getKmerSize();
		for (int i = 0; i < read.length() - kmerSize + 1; i++)
			DeBruijnGraph.getInstance().addEdge(read.substring(i, i + kmerSize - 1), read.substring(i + 1, i + kmerSize));
	}
	
	private static void printContigInFastaFormat(BufferedWriter writer, LinkedList<DirectedEdge> contigEdgeList, int contigCount, int kmerSize) 
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
	
	public static void generateContigs(String outputFile)
	{
		DeBruijnGraph graph;
		LinkedList<DirectedEdge> contigEdgeList = new LinkedList<DirectedEdge>();
		DirectedEdge unvisitedEdge;
		BufferedWriter writer;
		int contigCount = 0;
		boolean newContigEdgeAdded;
		try
		{
			graph = DeBruijnGraph.getInstance();
			writer = new BufferedWriter(new FileWriter(new File(outputFile)));
			
			while (true)
			{
				unvisitedEdge = graph.getZeroInDegreeUnvisitedEdge();
				if(unvisitedEdge==null)
				{
					unvisitedEdge = graph.getUnvisitedEdge();
					if(unvisitedEdge==null)
						break;
				}
				else
				{
					contigEdgeList.add(unvisitedEdge);
					unvisitedEdge.setVisited();
					newContigEdgeAdded = true;
					while (!contigEdgeList.isEmpty()) {
						unvisitedEdge = graph.getUnvisitedEdge(contigEdgeList.getLast().getEnd());
						if (unvisitedEdge!=null) {
							unvisitedEdge.setVisited();
							contigEdgeList.add(unvisitedEdge);
							newContigEdgeAdded = true;
						}
						else {
							if(newContigEdgeAdded) {
								contigCount++;
								printContigInFastaFormat(writer,contigEdgeList, contigCount, graph.getKmerSize());
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
