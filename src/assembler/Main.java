package assembler;
import java.io.File;
import java.util.ArrayList;

public class Main
{
	public enum AssemblyMethods { OVERLAP, GREEDY, DE_BRUIJN, IMPROVED_DE_BRUIJN; };
	
	static String SEQUENCE_FILE;// = "Borrelia-bissettii_shortened.fasta";
	static String READS_FILE_NAME;// = "generatedReads.fasta";
	static String GENERATED_CONTIGS_FILE_LOCATION;// = "generatedContigs.fasta";
	static int MINIMUM_OVERLAP_LENGTH = 10;
	static int K = 41;
	
	static long programStartTime, programEndTime;
	static ArrayList<AssemblyMethods> assemblyMethods = new ArrayList<AssemblyMethods>();
	
	static long dbgGraphConstructionStartTime = 0, dbgGraphConstructionEndTime = 0;
	static long overlapGraphConstructionStartTime = 0, overlapGraphConstructionEndTime = 0;
	static long greedyGraphConstructionStartTime = 0, greedyGraphConstructionEndTime = 0;
	static long impDbgGraphConstructionStartTime = 0, impDbgGraphConstructionEndTime = 0;
	
	static long dbgContigGenerationStartTime = 0, dbgContigGenerationEndTime = 0;
	static long overlapContigGenerationStartTime = 0, overlapContigGenerationEndTime = 0;
	static long greedyContigGenerationStartTime = 0, greedyContigGenerationEndTime = 0;
	static long impDbgContigGenerationStartTime = 0, impDbgContigGenerationEndTime = 0;
	
	public static boolean isCompleted = false;
	
	public static void main(String args[]) 
	{
		//addAssemblyMethod(AssemblyMethods.DE_BRUIJN);
		//addAssemblyMethod(AssemblyMethods.IMPROVED_DE_BRUIJN);
		
		programStartTime = System.nanoTime();
		isCompleted = false;
		for (AssemblyMethods method : assemblyMethods) {
			switch (method) {	
			case OVERLAP:
				System.out.println("Method: OverlapGraph");
				overlapGraphConstructionStartTime = System.nanoTime();
				OlcOverlapGraph oog = new OlcOverlapGraph(MINIMUM_OVERLAP_LENGTH);
				oog.constructGraph(new File(READS_FILE_NAME), MINIMUM_OVERLAP_LENGTH);
				overlapGraphConstructionEndTime = System.nanoTime();
				System.out.println("Time to construct graph(ms): " + (overlapGraphConstructionEndTime - overlapGraphConstructionStartTime) / 1000000);
				
				overlapContigGenerationStartTime = System.nanoTime();
				oog.traverseGraphToGenerateContigs(GENERATED_CONTIGS_FILE_LOCATION + "OLC_GeneratedContigs.fasta");
				overlapContigGenerationEndTime = System.nanoTime();
				System.out.println("Time to generate contigs(ms): " + (overlapContigGenerationEndTime - overlapContigGenerationStartTime) / 1000000);
				oog = null;
				System.gc();
				break;
				
			case GREEDY:
				System.out.println("Method: Greedy");
				greedyGraphConstructionStartTime = System.nanoTime();
				GreedyOverlapGraph gog = new GreedyOverlapGraph(MINIMUM_OVERLAP_LENGTH);
				gog.constructGraph(new File(READS_FILE_NAME), MINIMUM_OVERLAP_LENGTH);
				greedyGraphConstructionEndTime = System.nanoTime();
				System.out.println("Time to construct graph(ms): " + (greedyGraphConstructionEndTime - greedyGraphConstructionStartTime) / 1000000);
				
				greedyContigGenerationStartTime = System.nanoTime();
				gog.traverseGraphToGenerateContigs(GENERATED_CONTIGS_FILE_LOCATION + "GREEDY_GeneratedContigs.fasta");
				greedyContigGenerationEndTime = System.nanoTime();
				System.out.println("Time to generate contigs(ms): " + (greedyContigGenerationEndTime - greedyContigGenerationStartTime) / 1000000);
				gog = null;
				System.gc();
				break;
			
			case DE_BRUIJN:
				System.out.println("Method: DeBruijnGraph");
				
				dbgGraphConstructionStartTime = System.nanoTime();
				TypicalDeBruijnGraph dbg = new TypicalDeBruijnGraph();
				dbg.constructGraph(new File(READS_FILE_NAME), K);
				dbgGraphConstructionEndTime = System.nanoTime();
				System.out.println("Time to construct graph(ms): " + (dbgGraphConstructionEndTime - dbgGraphConstructionStartTime) / 1000000);
				
				dbgContigGenerationStartTime = System.nanoTime();
				dbg.traverseGraphToGenerateContigs(GENERATED_CONTIGS_FILE_LOCATION + "DBG_GeneratedContigs.fasta");
				dbgContigGenerationEndTime = System.nanoTime();
				System.out.println("Time to generate contigs(ms): " + (dbgContigGenerationEndTime - dbgContigGenerationStartTime) / 1000000);
				
				dbg = null;
				System.gc();
				break;
				
			case IMPROVED_DE_BRUIJN:
				System.out.println("Method: ImprovedDeBruijnGraph");
				impDbgGraphConstructionStartTime = System.nanoTime();
				ImprovedDeBruijnGraph idbg = new ImprovedDeBruijnGraph();
				idbg.constructGraph(new File(READS_FILE_NAME), K);
				impDbgGraphConstructionEndTime = System.nanoTime();
				System.out.println("Time to construct graph(ms): " + (impDbgGraphConstructionEndTime - impDbgGraphConstructionStartTime) / 1000000);
				
				impDbgContigGenerationStartTime = System.nanoTime();
				idbg.traverseGraphToGenerateContigs(GENERATED_CONTIGS_FILE_LOCATION + "ImprovedDBG_GeneratedContigs.fasta");
				impDbgContigGenerationEndTime = System.nanoTime();
				System.out.println("Time to generate contigs(ms): " + (impDbgContigGenerationEndTime - impDbgContigGenerationStartTime) / 1000000);
				idbg = null;
				System.gc();
				break;
				
			default:
				System.out.println("Graph contruction and contigs generation skipped.");
				break;
			}
			System.out.println();
		}
		
		isCompleted = true;
		programEndTime = System.nanoTime();
		System.out.println("Program execution time(ms): " + (programEndTime - programStartTime) / 1000000);
	}
	
	public static void setReadsFile(String filePath) { // TODO: requires possible changes to other codes!
		READS_FILE_NAME = filePath;
	}
	
	public static void setContigsFileLocation(String filePath) {
		GENERATED_CONTIGS_FILE_LOCATION = filePath;
	}
	
	public static void addAssemblyMethod(AssemblyMethods newMethod) {
		if (!assemblyMethods.contains(newMethod)) {
			assemblyMethods.add(newMethod);
		}
	}
	
	public static void setK(int k) {
		K = k;
	}
	
	public static void setMinimumOverlapLength(int mol) {
		MINIMUM_OVERLAP_LENGTH = mol;
	}
	
	public static int getNoOfAssemblyMethods() {
		return assemblyMethods.size();
	}
	
	public static void resetTime() {
		dbgGraphConstructionStartTime = 0;
		dbgGraphConstructionEndTime = 0;
		dbgContigGenerationStartTime = 0;
		dbgContigGenerationEndTime = 0;
		
		overlapGraphConstructionStartTime = 0;
		overlapGraphConstructionEndTime = 0;
		overlapContigGenerationStartTime = 0;
		overlapContigGenerationEndTime = 0;
		
		greedyGraphConstructionStartTime = 0;
		greedyGraphConstructionEndTime = 0;
		greedyContigGenerationStartTime = 0;
		greedyContigGenerationEndTime = 0;
		
		impDbgGraphConstructionStartTime = 0;
		impDbgGraphConstructionEndTime = 0;
		impDbgContigGenerationStartTime = 0;
		impDbgContigGenerationEndTime = 0;
	}
	
	public static long getGraphConstructionRunTime(AssemblyMethods assemblyMethod) {
		switch(assemblyMethod) {
		case OVERLAP:
			if (overlapGraphConstructionStartTime != 0 && overlapGraphConstructionEndTime == 0)
				return (System.nanoTime() - overlapGraphConstructionStartTime) / 1000000;
			else
				return (overlapGraphConstructionEndTime - overlapGraphConstructionStartTime) / 1000000;
		
		case GREEDY:
			if (greedyGraphConstructionStartTime != 0 && greedyGraphConstructionEndTime == 0)
				return (System.nanoTime() - greedyGraphConstructionStartTime) / 1000000;
			else
				return (greedyGraphConstructionEndTime - greedyGraphConstructionStartTime) / 1000000;
		
		case DE_BRUIJN:
			if (dbgGraphConstructionStartTime != 0 && dbgGraphConstructionEndTime == 0)
				return (System.nanoTime() - dbgGraphConstructionStartTime) / 1000000;
			else
				return (dbgGraphConstructionEndTime - dbgGraphConstructionStartTime) / 1000000;
		
		case IMPROVED_DE_BRUIJN:
			if (impDbgGraphConstructionStartTime != 0 && impDbgGraphConstructionEndTime == 0)
				return (System.nanoTime() - impDbgGraphConstructionStartTime) / 1000000;
			else
				return (impDbgGraphConstructionEndTime - impDbgGraphConstructionStartTime) / 1000000;
			
		default:
			return 0;
		}
	}
	
	public static long getContigGenerationRunTime(AssemblyMethods assemblyMethod) {
		switch(assemblyMethod) {
		
		case OVERLAP:
			if (overlapContigGenerationStartTime != 0 && overlapContigGenerationEndTime == 0)
				return (System.nanoTime() - overlapContigGenerationStartTime) / 1000000;
			else
				return (overlapContigGenerationEndTime - overlapContigGenerationStartTime) / 1000000;
		
		case GREEDY:
			if (greedyContigGenerationStartTime != 0 && greedyContigGenerationEndTime == 0)
				return (System.nanoTime() - greedyContigGenerationStartTime) / 1000000;
			else
				return (greedyContigGenerationEndTime - greedyContigGenerationStartTime) / 1000000;
		
		case DE_BRUIJN:
			if (dbgContigGenerationStartTime != 0 && dbgContigGenerationEndTime == 0)
				return (System.nanoTime() - dbgContigGenerationStartTime) / 1000000;
			else
				return (dbgContigGenerationEndTime - dbgContigGenerationStartTime) / 1000000;
			
		case IMPROVED_DE_BRUIJN:
			if (impDbgContigGenerationStartTime != 0 && impDbgContigGenerationEndTime == 0)
				return (System.nanoTime() - impDbgContigGenerationStartTime) / 1000000;
			else
				return (impDbgContigGenerationEndTime - impDbgContigGenerationStartTime) / 1000000;
			
		default:
			return 0;
		}
	}
}
