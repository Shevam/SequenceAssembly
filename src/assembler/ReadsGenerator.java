package assembler;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

public class ReadsGenerator
{	
	public static String generateReads(String sequenceFile, int readSize, int minOverlapLength,String readsFile)
	{
		String sequence;
		
		try (Scanner fileIn = new Scanner(new File(sequenceFile)))
		{
			String currentLine = "";
			StringBuilder sequenceBuilder = new StringBuilder();
			
			while (fileIn.hasNextLine())
			{
				currentLine = fileIn.nextLine();
				
				if (!currentLine.startsWith(">"))
					sequenceBuilder.append(currentLine.trim());
			}
			sequence = sequenceBuilder.toString();
		}
		catch (FileNotFoundException | NullPointerException e) {
			return sequenceFile + " not found. [" + e.getMessage() + "]";
		} catch (Exception e) {
			e.printStackTrace();
			return "An error occured while reading sequence. [" + e.getMessage() + "]";
		}
		
		if (sequence == null || sequence.equals("")) {
			return "Sequence file is invalid. Supported file format content example:\n"
				+ "\n"
				+ ">Sequence Name\n"
				+ "AGTCGAGCA...";
		}
		
		if (sequence.length()<readSize) {
			return "Sequence length (" + sequence.length() + ") is less than specified read size (" + readSize + ").";
		}
		
		BufferedWriter writer;
		try 
		{
			writer = new BufferedWriter(new FileWriter(new File(readsFile)));
			int aRandomNumber = 0, sequenceSection=1, readCount=0, lineLength=80;
			while(sequenceSection*readSize<sequence.length())
			{
				String circularRead;
				
				if (((sequenceSection)*readSize)+aRandomNumber <= sequence.length()){
					readCount++;circularRead = sequence.substring((sequenceSection-1)*readSize, sequenceSection*readSize);
					writer.write(">r" + readCount + "\n");
					for(int i=0;i<circularRead.length();i+=lineLength) {
						if(i+lineLength > circularRead.length())
							writer.write(circularRead.substring(i) + "\n");
						else
							writer.write(circularRead.substring(i, i+lineLength) + "\n");
					}
					readCount++;
					
					aRandomNumber = new Random().nextInt(readSize - minOverlapLength);
					circularRead = sequence.substring(((sequenceSection-1)*readSize)+aRandomNumber, (sequenceSection*readSize)+aRandomNumber);
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
					circularRead = sequence.substring(((sequenceSection-1)*readSize)+aRandomNumber);
					int remainingNoOfChars = readSize - circularRead.length();
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
			
			writer.close();
			return "Success! Number of reads generated: "+readCount;
		}
		catch (IOException | NullPointerException e) {
			e.printStackTrace();
			return "Error in creating, opening or writing output file. [" + e.getMessage() + "]";
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
			return "Minimum overlap length cannot be greater or equal to read size.";
		}
		catch (Exception e) {
			e.printStackTrace();
			return "Sorry! An error has occurred while generating reads. [" + e.getMessage() + "]";
		}
	}
}
