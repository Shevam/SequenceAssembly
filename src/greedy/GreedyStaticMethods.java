package greedy;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Scanner;

public class GreedyStaticMethods
{
	private final static int fastaLineLength = 80;
	
	public static String getOverlap(String startString, String endString, int minimumOverlapLength) {
		int endIndex = endString.length() - 1;
		while (endIndex >= minimumOverlapLength	&& !startString.endsWith(endString.substring(0, endIndex)))
			endIndex--;
		if(!startString.endsWith(endString.substring(0, endIndex)))
			return null;
		return endString.substring(0, endIndex);
	}
	
	public static String getSuperString(String startString, String endString)
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
	
	private static void printContigInFastaFormat(BufferedWriter writer, LinkedList<Node> contigNodeList, int contigCount)
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
				contigPart = contigNodeList.get(j).getRead().substring(OverlapGraph.getInstance().getOverlapLength(contigNodeList.get(j-1), contigNodeList.get(j)));
				
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

	public static void generateContigs(String outputFile) 
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
				currentNode = OverlapGraph.getInstance().getLeastIndegreeUnvisitedNode();
				if(currentNode == null)
					break;
				
		        while(currentNode != null)
		        {
		        	contigNodeList.add(currentNode);
			        currentNode.setVisited(true);
			        currentNode = OverlapGraph.getInstance().getNextNodeWithHighestOverlapLength(currentNode);
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
