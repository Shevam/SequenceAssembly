package interfaces;
import java.io.File;

public interface IGraph {
	static int fastaLineLength = 80;
	
	public void constructGraph(File readsFile, int kmerSize);
	public void generateContigs(String outputFile);
}