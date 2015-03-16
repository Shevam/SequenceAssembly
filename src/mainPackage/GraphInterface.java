package mainPackage;

import java.io.File;

public interface GraphInterface {
	
	public void constructGraph(File readsFile, int kmerSize);
	public void generateContigs(String outputFile);
}
