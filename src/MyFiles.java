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
class MyFiles {
	
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
	 * <p>Reads terrain data from a file in format shown below. Data is written into
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
			if (outFile.createNewFile()) {
				System.out.println("File "+filename+" created.");
			}
			else {
				System.out.println("File "+filename+" found.");
				if (outFile.length() != 0L) { // check if file is empty
					System.out.println("File "+filename+" not empty. "
							+ "Contents will be overwritten.");
				}
			}
			
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
	 * <p>Writes speed test data to file.</p>
	 * 
	 * @param data
	 * @param seqCutoffs
	 * @param seqPar
	 * @param usePathPrefix
	 */
	public static void compileTestData(double[][] data, int[] seqCutoffs, String seqPar, boolean usePathPrefix) {
		/* FIXME:
		 * How can this file always be put in to ROOT/io-files?
		 */
		String pp;
		if (usePathPrefix) {pp="io-files/";}
		else {pp="";}
		
		String dataSize = dataDims[0]+"x"+dataDims[1];
		
		String filename = pp + dataSize + "_" + seqPar + ".txt";
		
		try {
			File f = new File(filename);
			
			if (f.createNewFile()) {} // create file
			else { // file already exists
				FileWriter wTemp = new FileWriter(f);
				wTemp.write(""); // clear file
				wTemp.close();
			}
			
			FileWriter w = new FileWriter(f, true);
			w.write("# Speed Test Data\n# Units: ns\n# Process: "+seqPar+"\n# Data size: "+dataSize+"\n");
			
			for (int c=0; c<data.length; c++) {
				w.write("\n# Sequential cutoff: "+seqCutoffs[c]+"\n");
				w.write("# min: "+trunc(Stats.min(data[c]),8)+
						"\n# max: "+trunc(Stats.max(data[c]),8)+
						"\n# mean: "+trunc(Stats.mean(data[c]),8)+"\n");
				for (int i=0; i<data[0].length; i++) {
					w.write(trunc(data[c][i],8) + "\n");
				}
			}
			
			w.close();
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
}

