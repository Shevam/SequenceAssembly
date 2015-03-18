package assembler;
import java.io.File;

public class Main 
{
	public enum AssemblyMethods { DE_BRUIJN, OVERLAP, GREEDY, IMPROVED_DE_BRUIJN };
	
	static final String SEQUENCE_FILE = "BorreliaFull_CompleteSequence.fasta";
	static final String READS_FILE_NAME = "generatedReads.fasta";
	static final String GENERATED_CONTIGS_FILE = "generatedContigs.fasta";
	static final int MINIMUM_OVERLAP_LENGTH = 1;
	static final int KMER_SIZE = 41;
	
	static long programStartTime, programEndTime;
	static long graphConstructionStartTime, graphConstructionEndTime;
	static long contigGenerationStartTime, contigGenerationEndTime;
	static AssemblyMethods assemblyMethods[];
	
	public static void main(String args[]) 
	{
		assemblyMethods = new AssemblyMethods[] { AssemblyMethods.GREEDY };//, AssemblyMethods.OVERLAP, AssemblyMethods.DE_BRUIJN, AssemblyMethods.IMPROVED_DE_BRUIJN };
		
		programStartTime = System.nanoTime();
		
		for (AssemblyMethods method : assemblyMethods) {
			switch (method) {
			case DE_BRUIJN:
				System.out.println("Method: DeBruijnGraph");
				
				graphConstructionStartTime = System.nanoTime();
				TypicalDeBruijnGraph dbg = new TypicalDeBruijnGraph();
				dbg.constructGraph(new File(READS_FILE_NAME), KMER_SIZE);
				graphConstructionEndTime = System.nanoTime();
				System.out.println("Time to construct graph(ms): " + (graphConstructionEndTime - graphConstructionStartTime) / 1000000);
				
				contigGenerationStartTime = System.nanoTime();
				dbg.generateContigs(GENERATED_CONTIGS_FILE);
				contigGenerationEndTime = System.nanoTime();
				System.out.println("Time to generate contigs(ms): " + (contigGenerationEndTime - contigGenerationStartTime) / 1000000);
				
				dbg = null;
				System.gc();
				break;
				
			case OVERLAP:
				System.out.println("Method: OverlapGraph");
				graphConstructionStartTime = System.nanoTime();
				OlcOverlapGraph oog = new OlcOverlapGraph(MINIMUM_OVERLAP_LENGTH);
				oog.constructGraph(new File(READS_FILE_NAME), MINIMUM_OVERLAP_LENGTH);
				graphConstructionEndTime = System.nanoTime();
				System.out.println("Time to construct graph(ms): " + (graphConstructionEndTime - graphConstructionStartTime) / 1000000);
				
				contigGenerationStartTime = System.nanoTime();
				oog.generateContigs(GENERATED_CONTIGS_FILE);
				contigGenerationEndTime = System.nanoTime();
				System.out.println("Time to generate contigs(ms): " + (contigGenerationEndTime - contigGenerationStartTime) / 1000000);
				oog = null;
				System.gc();
				break;
				
			case GREEDY:
//				System.out.println("Method: Greedy");
//				graphConstructionStartTime = System.nanoTime();
//				GreedyOverlapGraph gog = new GreedyOverlapGraph(MINIMUM_OVERLAP_LENGTH);
//				gog.constructGraph(new File(READS_FILE_NAME), MINIMUM_OVERLAP_LENGTH);
//				graphConstructionEndTime = System.nanoTime();
//				System.out.println("Time to construct graph(ms): " + (graphConstructionEndTime - graphConstructionStartTime) / 1000000);
//				
//				contigGenerationStartTime = System.nanoTime();
//				gog.generateContigs(GENERATED_CONTIGS_FILE);
//				contigGenerationEndTime = System.nanoTime();
//				System.out.println("Time to generate contigs(ms): " + (contigGenerationEndTime - contigGenerationStartTime) / 1000000);
//				gog = null;
//				System.gc();
				
				Thread t = new Thread() {
					@Override
					public void run() {
						super.run();
						System.out.println("Method: Greedy");
						graphConstructionStartTime = System.nanoTime();
						GreedyOverlapGraph gog = new GreedyOverlapGraph(MINIMUM_OVERLAP_LENGTH);
						gog.constructGraph(new File(READS_FILE_NAME), MINIMUM_OVERLAP_LENGTH);
						graphConstructionEndTime = System.nanoTime();
						System.out.println("Time to construct graph(ms): " + (graphConstructionEndTime - graphConstructionStartTime) / 1000000);
						
						contigGenerationStartTime = System.nanoTime();
						gog.generateContigs(GENERATED_CONTIGS_FILE);
						contigGenerationEndTime = System.nanoTime();
						System.out.println("Time to generate contigs(ms): " + (contigGenerationEndTime - contigGenerationStartTime) / 1000000);
						gog = null;
						System.gc();
					}
					
					public Thread initialize() {
						return this;
					}
				}.initialize();
				t.start();
				long prev = 0, current;
				while (t.isAlive()) {
					current = java.lang.management.ManagementFactory.getThreadMXBean().getThreadCpuTime(t.getId());
					if (current != -1)
						prev = current;
				}
				System.out.println("Thread time(ms) = " + (prev/1000000));
				break;
				
			case IMPROVED_DE_BRUIJN:
				System.out.println("Method: ImprovedDeBruijnGraph");
				graphConstructionStartTime = System.nanoTime();
				ImprovedDeBruijnGraph idbg = new ImprovedDeBruijnGraph();
				idbg.constructGraph(new File(READS_FILE_NAME), KMER_SIZE);
				graphConstructionEndTime = System.nanoTime();
				System.out.println("Time to construct graph(ms): " + (graphConstructionEndTime - graphConstructionStartTime) / 1000000);
				//DeBruijnGraph.getInstance().displayGraph();
				contigGenerationStartTime = System.nanoTime();
				idbg.generateContigs(GENERATED_CONTIGS_FILE);
				contigGenerationEndTime = System.nanoTime();
				System.out.println("Time to generate contigs(ms): " + (contigGenerationEndTime - contigGenerationStartTime) / 1000000);
				idbg = null;
				System.gc();
				break;
				
			default:
				System.out.println("Graph contruction and contigs generation skipped.");
				break;
			}
		}
		
		programEndTime = System.nanoTime();
		System.out.println("Program execution time(ms): " + (programEndTime - programStartTime) / 1000000);
	}
}
