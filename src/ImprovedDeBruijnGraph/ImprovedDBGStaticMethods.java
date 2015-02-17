package ImprovedDeBruijnGraph;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ImprovedDBGStaticMethods
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
			
			//TODO implement DEPTH FIRST SEARCH(DFS) 19. GraphAlgos
			while (fileIn.hasNextLine())
			{
				currentLine = fileIn.nextLine();

				if (currentLine.startsWith(">")) {
					if (!read.toString().equals("")) {
						addKmersToGraph(read.toString().toUpperCase());
						readCount++;
					}
					read = new StringBuilder();
				} 
				else
					read.append(currentLine.trim());
			}

			if (!read.toString().equals("")) {
				addKmersToGraph(read.toString().toUpperCase());
				readCount++;
			}

			System.out.println("Number of reads processed: " + readCount);
		}
		catch (FileNotFoundException e) {
			System.err.println("File not found: " + readsFile);
		}
	}
	
	public static void addKmersToGraph(String read) 
	{
		int kmerSize = DeBruijnGraph.getInstance().getKmerSize();
		for (int i = 0; i < read.length() - kmerSize + 1; i++)
			DeBruijnGraph.getInstance().addEdge(read.substring(i, i + kmerSize - 1), read.substring(i + 1, i + kmerSize));
	}

	public static void generateContigs(String generatedContigsFile)
	{
		DirectedEdge unvisitedEdge;
		BufferedWriter writer;
		TraversalThread traversalThread;
		ExecutorService es;
		boolean finished;
		DeBruijnGraph g;
		
		try
		{
			writer = new BufferedWriter(new FileWriter(new File(generatedContigsFile)));
			new WriterThread(writer, DeBruijnGraph.getInstance().getKmerSize());
			
			es = Executors.newFixedThreadPool(8);
			while (true)
			{
				g = DeBruijnGraph.getInstance();
				while(g==null)
					g = DeBruijnGraph.getInstance();
				
				unvisitedEdge = g.getZeroInDegreeUnvisitedEdge();
				
				if(unvisitedEdge==null)
					break;
				
				unvisitedEdge.setVisited(true);
				es.execute(traversalThread = new TraversalThread(unvisitedEdge));
			}
			es.shutdown();
			finished = es.awaitTermination(30, TimeUnit.MINUTES);
			
			if(!finished)
				System.out.println("timeout elapsed before thread termination!!");
			
			g = DeBruijnGraph.getInstance();
			while(g==null)
				g = DeBruijnGraph.getInstance();
			
			unvisitedEdge = g.getUnvisitedEdge();
			while(unvisitedEdge!=null)
			{
				traversalThread = new TraversalThread(unvisitedEdge);
				traversalThread.start();
				traversalThread.join();
				
				g = DeBruijnGraph.getInstance();
				while(g==null)
					g = DeBruijnGraph.getInstance();
				
				unvisitedEdge = g.getUnvisitedEdge();
			}
			writer.close();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
