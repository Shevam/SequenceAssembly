package mainPackage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Random;
import java.util.Scanner;

import javax.swing.JOptionPane;

public class ReadsGenerator
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
		catch (FileNotFoundException | NullPointerException e) {
			JOptionPane.showMessageDialog(null, SEQUENCE_FILE + " not found. [" + e.getMessage() + "]");
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String generateReads(String SEQUENCE_FILE, int READ_SIZE, int MINIMUM_OVERLAP_LENGTH,String OUTPUT_FILE)
	{
		String sequence = getSequence(SEQUENCE_FILE);
		BufferedWriter writer;
		if(sequence != null && !sequence.equals("") && sequence.length()>READ_SIZE)
		{
			try 
			{
				writer = new BufferedWriter(new FileWriter(new File(OUTPUT_FILE)));
				int sequenceSection=0, readCount=0, lineLength=80;
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
				
				writer.close();
				return "Success! Number of reads generated: "+readCount;
			}
			catch (Exception e) {
				return e.getMessage();
			}
		} else {
			return "Please review sequence file. It may be an invalid fasta file.";
		}
	}
}
