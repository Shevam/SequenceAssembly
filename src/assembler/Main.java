package assembler;
import java.io.File;

public class Main 
{
	public enum AssemblyMethods { DE_BRUIJN, OVERLAP, GREEDY,IMPROVED_DE_BRUIJN; };
	
	static final String SEQUENCE_FILE = "BorreliaFull_CompleteSequence.fasta";
	static final String READS_FILE_NAME = "generatedReads.fasta";
	static final String GENERATED_CONTIGS_FILE = "generatedContigs.fasta";
	static final int MINIMUM_OVERLAP_LENGTH = 10;
	static final int KMER_SIZE = 41;
	
	static long programStartTime, programEndTime;
	static AssemblyMethods assemblyMethods[];
	
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
		assemblyMethods = new AssemblyMethods[] { AssemblyMethods.GREEDY, AssemblyMethods.OVERLAP };
		
		programStartTime = System.nanoTime();
		isCompleted = false;
		for (AssemblyMethods method : assemblyMethods) {
			switch (method) {
			case DE_BRUIJN:
				System.out.println("Method: DeBruijnGraph");
				
				dbgGraphConstructionStartTime = System.nanoTime();
				TypicalDeBruijnGraph dbg = new TypicalDeBruijnGraph();
				dbg.constructGraph(new File(READS_FILE_NAME), KMER_SIZE);
				dbgGraphConstructionEndTime = System.nanoTime();
				System.out.println("Time to construct graph(ms): " + (dbgGraphConstructionEndTime - dbgGraphConstructionStartTime) / 1000000);
				
				dbgContigGenerationStartTime = System.nanoTime();
				dbg.traverseGraphToGenerateContigs(GENERATED_CONTIGS_FILE);
				dbgContigGenerationEndTime = System.nanoTime();
				System.out.println("Time to generate contigs(ms): " + (dbgContigGenerationEndTime - dbgContigGenerationStartTime) / 1000000);
				
				dbg = null;
				System.gc();
				break;
				
			case OVERLAP:
				System.out.println("Method: OverlapGraph");
				overlapGraphConstructionStartTime = System.nanoTime();
				OlcOverlapGraph oog = new OlcOverlapGraph(MINIMUM_OVERLAP_LENGTH);
				oog.constructGraph(new File(READS_FILE_NAME), MINIMUM_OVERLAP_LENGTH);
				overlapGraphConstructionEndTime = System.nanoTime();
				System.out.println("Time to construct graph(ms): " + (overlapGraphConstructionEndTime - overlapGraphConstructionStartTime) / 1000000);
				
				overlapContigGenerationStartTime = System.nanoTime();
				oog.traverseGraphToGenerateContigs(GENERATED_CONTIGS_FILE);
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
				gog.traverseGraphToGenerateContigs(GENERATED_CONTIGS_FILE);
				greedyContigGenerationEndTime = System.nanoTime();
				System.out.println("Time to generate contigs(ms): " + (greedyContigGenerationEndTime - greedyContigGenerationStartTime) / 1000000);
				gog = null;
				System.gc();
				break;
				
			case IMPROVED_DE_BRUIJN:
				System.out.println("Method: ImprovedDeBruijnGraph");
				impDbgGraphConstructionStartTime = System.nanoTime();
				ImprovedDeBruijnGraph idbg = new ImprovedDeBruijnGraph();
				idbg.constructGraph(new File(READS_FILE_NAME), KMER_SIZE);
				impDbgGraphConstructionEndTime = System.nanoTime();
				System.out.println("Time to construct graph(ms): " + (impDbgGraphConstructionEndTime - impDbgGraphConstructionStartTime) / 1000000);
				//DeBruijnGraph.getInstance().displayGraph();
				impDbgContigGenerationStartTime = System.nanoTime();
				idbg.traverseGraphToGenerateContigs(GENERATED_CONTIGS_FILE);
				impDbgContigGenerationEndTime = System.nanoTime();
				System.out.println("Time to generate contigs(ms): " + (impDbgContigGenerationEndTime - impDbgContigGenerationStartTime) / 1000000);
				idbg = null;
				System.gc();
				break;
				
			default:
				System.out.println("Graph contruction and contigs generation skipped.");
				break;
			}
		}
		
		isCompleted = true;
		programEndTime = System.nanoTime();
		System.out.println("Program execution time(ms): " + (programEndTime - programStartTime) / 1000000);
	}
	
	public static long getGraphConstructionRunTime(AssemblyMethods assemblyMethod) {
		switch(assemblyMethod) {
		case DE_BRUIJN:
			if (dbgGraphConstructionStartTime != 0 && dbgGraphConstructionEndTime == 0)
				return (System.nanoTime() - dbgGraphConstructionStartTime) / 1000000;
			else
				return (dbgGraphConstructionEndTime - dbgGraphConstructionStartTime) / 1000000;
		
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
		case DE_BRUIJN:
			if (dbgContigGenerationStartTime != 0 && dbgContigGenerationEndTime == 0)
				return (System.nanoTime() - dbgContigGenerationStartTime) / 1000000;
			else
				return (dbgContigGenerationEndTime - dbgContigGenerationStartTime) / 1000000;
		
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
