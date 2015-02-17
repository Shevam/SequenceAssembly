package DeBruijnGraph;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

public class DebruijnGraphStaticMethods
{
	public static String getSequence(String SEQUENCE_FILE)
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
				int sequenceSection=0, readCount=0, lineLength=80, aRandomNumber;
				sequenceSection++;
				aRandomNumber = new Random().nextInt(READ_SIZE - 2*MINIMUM_OVERLAP_LENGTH);
				aRandomNumber += MINIMUM_OVERLAP_LENGTH;
				while(sequenceSection*READ_SIZE<sequence.length())
				{
					String circularRead;
					aRandomNumber = new Random().nextInt(READ_SIZE - MINIMUM_OVERLAP_LENGTH);
					
					if (((sequenceSection)*READ_SIZE)+aRandomNumber <= sequence.length()){
						readCount++;
						circularRead = sequence.substring(((sequenceSection-1)*READ_SIZE)+aRandomNumber, (sequenceSection*READ_SIZE)+aRandomNumber);
						writer.write(">r" + readCount + "\n");
						for(int i=0;i<circularRead.length();i+=lineLength) {
							if(i+lineLength > circularRead.length())
								writer.write(circularRead.substring(i) + "\n");
							else
								writer.write(circularRead.substring(i, i+lineLength) + "\n");
						}
						
						readCount++;
						circularRead = sequence.substring((sequenceSection-1)*READ_SIZE, sequenceSection*READ_SIZE);
						writer.write(">r" + readCount + "\n");
						for(int i=0;i<circularRead.length();i+=lineLength) {
							if(i+lineLength > circularRead.length())
								writer.write(circularRead.substring(i) + "\n");
							else
								writer.write(circularRead.substring(i, i+lineLength) + "\n");
						}
						sequenceSection++;
					}
					else {
						readCount++;
						circularRead = sequence.substring(((sequenceSection-1)*READ_SIZE)+aRandomNumber);
						int remainingNoOfChars = READ_SIZE - circularRead.length();
						circularRead += sequence.substring(0, remainingNoOfChars);
						writer.write(">r" + readCount + "\n");
						for(int i=0;i<circularRead.length();i+=lineLength) {
							if(i+lineLength > circularRead.length())
								writer.write(circularRead.substring(i) + "\n");
							else
								writer.write(circularRead.substring(i, i+lineLength) + "\n");
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
	
	public static void simplifyGraph() 
	{
		
	}
	
	public static void generateContigs(String OUTPUT_FILE)
	{
		DeBruijnGraph graph;
		LinkedList<DirectedEdge> contigEdgeList = new LinkedList<DirectedEdge>();
		DirectedEdge unvisitedEdge;
		BufferedWriter writer;
		int contigCount = 0;
		
		try
		{
			graph = DeBruijnGraph.getInstance();
			writer = new BufferedWriter(new FileWriter(new File(OUTPUT_FILE)));
			
			while (true)
			{
				unvisitedEdge = graph.getZeroInDegreeUnvisitedEdge();
				if(unvisitedEdge==null)
					unvisitedEdge = graph.getUnvisitedEdge();
				
				if(unvisitedEdge==null)
					break;
				else
				{
					DirectedEdge edge = unvisitedEdge;
					while (edge!=null)
					{
						edge.setVisited(true);
						contigEdgeList.add(edge);
						edge = graph.getUnvisitedEdge(edge.getEnd());
					}
					
					if(!contigEdgeList.isEmpty())
					{
						contigCount++;
						
						writer.write(contigEdgeList.getFirst().getKmer());
						for(int i=1; i<contigEdgeList.size(); i++)
							writer.write(contigEdgeList.get(i).getEnd().getKm1mer().charAt(graph.getKmerSize()-2));
						
						writer.flush();
						writer.write("\n");
					}
					
					while (!contigEdgeList.isEmpty())
					{
						edge = contigEdgeList.removeLast();
						edge = graph.getUnvisitedEdge(edge.getStart());
						if(edge!=null)
						{
							while (edge!=null) 
							{
								edge.setVisited(true);
								contigEdgeList.add(edge);
								edge = graph.getUnvisitedEdge(edge.getEnd());
							}
							
							if(!contigEdgeList.isEmpty())
							{
								contigCount++;
								writer.write(">c" + contigCount + ".1_EdgeCount_"+ contigEdgeList.size() +"\n");
								writer.write(contigEdgeList.getFirst().getKmer());
								for(int i=1; i<contigEdgeList.size(); i++)
									writer.write(contigEdgeList.get(i).getEnd().getKm1mer().charAt(graph.getKmerSize()-2));

								writer.flush();
								writer.write("\n");
							}
						}
					}
				}
			}
			writer.close();
			System.out.println("Number of contigs generated: "+contigCount);
		}
		catch (Exception e) {
			System.err.println("File not found: " + OUTPUT_FILE);
		}
	}
}
