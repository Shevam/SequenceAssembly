package mainPackage;
import java.io.File;

import DeBruijnGraph.DebruijnGraphStaticMethods;
import Hybrid.HybridStaticMethods;
import ImprovedDeBruijnGraph.ImprovedDBGStaticMethods;
import OverlapGraph.OverlapGraphStaticMethods;

public class Assembler 
{
	public enum GraphMethods { DE_BRUIJN, OVERLAP, HYBRID, IMPROVED_DE_BRUIJN };
	
	static final String SEQUENCE_FILE = "BorreliaFull_CompleteSequence.fasta";
	static final String GENERATED_READS_FILE = "generatedReads.fasta";
	static final String GENERATED_CONTIGS_FILE = "generatedContigs.fasta";
	static final String READS_FILE_NAME = GENERATED_READS_FILE;
	static final int READ_SIZE = 450;
	static final int MINIMUM_OVERLAP_LENGTH = 10;
	static final int KMER_SIZE = 25;
	
	static long programStartTime, programEndTime;
	static long graphConstructionStartTime, graphConstructionEndTime;
	static long contigGenerationStartTime, contigGenerationEndTime;
	static GraphMethods graphMethod;
	
	public static void main(String args[]) 
	{
		graphMethod = GraphMethods.DE_BRUIJN;
		
		programStartTime = System.nanoTime();
		
		switch (graphMethod) {
		case DE_BRUIJN:
			System.out.println("Method: DeBruijnGraph");
			graphConstructionStartTime = System.nanoTime();
			DebruijnGraphStaticMethods.constructGraph(new File(READS_FILE_NAME), KMER_SIZE);
			graphConstructionEndTime = System.nanoTime();
			System.out.println("Time to construct graph(ms): " + (graphConstructionEndTime - graphConstructionStartTime) / 1000000);
			
			contigGenerationStartTime = System.nanoTime();
			DebruijnGraphStaticMethods.generateContigs(GENERATED_CONTIGS_FILE);
			contigGenerationEndTime = System.nanoTime();
			System.out.println("Time to generate contigs(ms): " + (contigGenerationEndTime - contigGenerationStartTime) / 1000000);
			break;
			
		case OVERLAP:
			System.out.println("Method: OverlapGraph");
			graphConstructionStartTime = System.nanoTime();
			OverlapGraphStaticMethods.constructGraph(new File(READS_FILE_NAME), MINIMUM_OVERLAP_LENGTH);
			graphConstructionEndTime = System.nanoTime();
			System.out.println("Time to construct graph(ms): " + (graphConstructionEndTime - graphConstructionStartTime) / 1000000);
			
			contigGenerationStartTime = System.nanoTime();
			OverlapGraphStaticMethods.generateContigs(GENERATED_CONTIGS_FILE);
			contigGenerationEndTime = System.nanoTime();
			break;
			
		case HYBRID:
			System.out.println("Method: Hybrid");
			graphConstructionStartTime = System.nanoTime();
			HybridStaticMethods.constructGraph(new File(READS_FILE_NAME), MINIMUM_OVERLAP_LENGTH);
			graphConstructionEndTime = System.nanoTime();
			System.out.println("Time to construct graph(ms): " + (graphConstructionEndTime - graphConstructionStartTime) / 1000000);
			
			contigGenerationStartTime = System.nanoTime();
			HybridStaticMethods.generateContigs(GENERATED_CONTIGS_FILE);
			contigGenerationEndTime = System.nanoTime();
			System.out.println("Time to generate contigs(ms): " + (contigGenerationEndTime - contigGenerationStartTime) / 1000000);
			break;
			
		case IMPROVED_DE_BRUIJN:
			System.out.println("Method: ImprovedDeBruijnGraph");
			graphConstructionStartTime = System.nanoTime();
			ImprovedDBGStaticMethods.constructGraph(new File(READS_FILE_NAME), KMER_SIZE);
			graphConstructionEndTime = System.nanoTime();
			System.out.println("Time to construct graph(ms): " + (graphConstructionEndTime - graphConstructionStartTime) / 1000000);
			
			contigGenerationStartTime = System.nanoTime();
			ImprovedDBGStaticMethods.generateContigs(GENERATED_CONTIGS_FILE);
			contigGenerationEndTime = System.nanoTime();
			System.out.println("Time to generate contigs(ms): " + (contigGenerationEndTime - contigGenerationStartTime) / 1000000);
			break;
			
		default:
			System.out.println("Graph contruction and contigs generation skipped.");
			break;
		}
		
		programEndTime = System.nanoTime();
		System.out.println("Program execution time(ms): " + (programEndTime - programStartTime) / 1000000);
	}
}
