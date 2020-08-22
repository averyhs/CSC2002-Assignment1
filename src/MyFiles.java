import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * File handling functions for Assignment1
 * 
 * @author hrrhan002
 *
 */
public class MyFiles {
	
	/**
	 * <p>Dimensions of terrain data grid of input file.</p>
	 */
	private static int[] dataDims = new int[2];
	
	/**
	 * <p>Accessor for dataDims (dimensions of terrain data).</p>
	 * @return [num_rows, num_cols]
	 */
	public static int[] getDataDims() {
		return dataDims;
	}
	
	/**
	 * <p>Reads terrain data from a file. Data is written into
	 * a {@link PointElevation} array. Stores dimensions of the grid of data to dataDims field.</p>
	 * <p> Required file format:<br> <terrain num rows – INT> <terrain num cols – INT> <br>
	 * <height at grid pos (0,0) - FLOAT> <height at grid pos (0,1) - FLOAT> ... etc.</p>
	 * @param filename
	 * @return PointElevation array with data from file
	 */
	public static PointElevation[] extractTerrainData(String filename) {
		try {
			// IO objects
			File inFile = new File(filename);
			Scanner inScanner = new Scanner(inFile);
			
			/* XXX:
			 * Assumption is that files are well formed. No hasNext*() checks.
			 */
			
			// get dimensions
			dataDims[0] = inScanner.nextInt();
			dataDims[1] = inScanner.nextInt();
			
			// create empty grid with dims
			PointElevation[] map = new PointElevation[dataDims[0]*dataDims[1]];
			
			// populate grid
			for (int i=0; i<dataDims[0]*dataDims[1]; i++) {
				map[i] = new PointElevation(inScanner.nextFloat());
			}
			inScanner.close();
			return map;
		}
		catch(FileNotFoundException e) { // very general exception handling
			System.out.println("Error opening or reading file "+filename);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * <p>Writes basin data to output file with the given name.</p>
	 * <p>Note that if the file already exists, it will be overwritten.</p>
	 * 
	 * @param total Total number of basins listed
	 * @param coords List of coordinates for each basin
	 * @param filename Name of output file
	 */
	public static void compileTerrainData(int total, int[][] coords, String filename) {
		try {
			File outFile = new File(filename);
			
			// create file if it doesn't exist
			outFile.createNewFile();
			
			FileWriter outWriter = new FileWriter(filename);
			
			// write total and coords to file
			outWriter.write(total + "\n");
			for (int i=0; i<coords.length; i++) {
				outWriter.write(coords[i][0] + " " + coords[i][1] + "\n");
			}
			
			outWriter.close();
		}
		catch (IOException e) { // very general exception handling
			e.printStackTrace();
		}
	}
	
	/**
	 * <p>Writes benchmarking data to file.</p>
	 * 
	 * @param data
	 * @param seqCutoffs
	 * @param seqPar
	 * @param usePathPrefix
	 */
	public static void compileTestData(double[][] dataSeq, double[][] dataPar, int[] seqCutoffs, String step, boolean usePathPrefix) {
		/* FIXME:
		 * How can this file always be put in to ROOT/io-files?
		 */
		String pp; // path prefix
		if (usePathPrefix) {pp="io-files/";}
		else {pp="";}
		
		String dataSize = dataDims[0]+"x"+dataDims[1]; // dimensions of data, <row>x<col>
		int length = dataDims[0]*dataDims[1];
		
		String filename = pp + dataSize + "_benchmarking_"+ step + ".txt";
		String plot_filename = pp + dataSize + "_plot_" + step + ".txt";
		String plot_m_filename = pp + dataSize + "_plot_means_" + step + ".txt";
		
		try {
			// files
			File f = new File(filename);
			File plot_f = new File(plot_filename);
			File plot_m_f = new File(plot_m_filename);
			
			// if files don't exist, create them
			// if files do exist, clear them
			if (f.createNewFile()) {}
			else {clearFile(f);}
			if (plot_f.createNewFile()) {}
			else {clearFile(plot_f);}
			if (plot_m_f.createNewFile()) {}
			else {clearFile(plot_m_f);}
			
			// create writers
			FileWriter w = new FileWriter(f, true);
			FileWriter plot_w = new FileWriter(plot_f, true);
			FileWriter plot_m_w = new FileWriter(plot_m_f, true);
			
			// write headers
			w.write("# Speed Test Data\n# Units: ms\n# Data size: "+dataSize+"\n# format: <SequentialTime>  <ParallelTime>\n");
			plot_w.write("# NumThreads Speedup \n");
			plot_m_w.write("# NumThreads SpeedupMean \n");
			
			double[] speedup = new double[dataSeq[0].length]; // for calculating means
			int numThreads; // for calculated number of threads
			
			// write data (// with no errors, dataPar.length==dataSeq.length)
			for (int c=0; c<dataPar.length; c++) {
				w.write("\n# Sequential cutoff: "+seqCutoffs[c]+"\n");
				w.write(String.format("# min: %s, %s",trunc(Stats.min(dataSeq[c]),8),trunc(Stats.min(dataPar[c]),8))+
						String.format("\n# max: %s, %s",trunc(Stats.max(dataSeq[c]),8),trunc(Stats.max(dataPar[c]),8))+
						String.format("\n# mean: %s, %s\n",trunc(Stats.mean(dataSeq[c]),8),trunc(Stats.mean(dataPar[c]),8)));
				
				for (int a=0; a<dataSeq[c].length; a++) {speedup[a] = dataSeq[c][a]/dataPar[c][a];} // get the speedup values to calculate the mean
				double speedupMean = Stats.mean(speedup);
				numThreads = numThreads(length,seqCutoffs[c]);
				
				plot_m_w.write(String.format("%s %s\n",numThreads,speedupMean));
				for (int i=0; i<dataPar[0].length; i++) {
					w.write(String.format("%-12s%-8s\n",trunc(dataSeq[c][i],8),trunc(dataPar[c][i],8)));
					plot_w.write(String.format("%s %s\n",numThreads,speedup[i]));
				}
			}
			// close writer
			w.close();
			plot_w.close();
			plot_m_w.close();
		}
		catch(IOException e) { // very general exception handling
			e.printStackTrace();
		}
	}
	
	private static void clearFile(File f) {
		try {
			FileWriter wTemp = new FileWriter(f);
			wTemp.write(""); // clear file
			wTemp.close();
		}
		catch(IOException e) { // very general exception handling
			e.printStackTrace();
		}
	}
	
	private static String trunc(double val, int len) {
		// NOTE: precision and accuracy lost is negligible in this application
		String str = String.valueOf(val);
		if (str.length() > len) {
			return str.substring(0,len);
		}
		else {
			return str;
		}
	}
	
	private static int numThreads(int length, int cutoff) {
		double log = Math.log(length/cutoff)/Math.log(2);
		return (int)Math.pow(2,Math.ceil(log));
	}
}

