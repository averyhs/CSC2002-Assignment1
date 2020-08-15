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
	 * <p>Reads terrain data from a file in format shown below. Data is written into
	 * a {@link PointElevation} array.</p>
	 * <p> Required file format:<br> <terrain num rows – INT> <terrain num cols – INT> <br>
	 * <height at grid pos (0,0) - FLOAT> <height at grid pos (0,1) - FLOAT> ... etc.</p>
	 * @param filename
	 * @return PointElevation grid with data from file
	 */
	public static PointElevation[][] extractTerrainData(String filename) {
		try {
			// IO objects
			File inFile = new File(filename);
			Scanner inScanner = new Scanner(inFile);
			
			/* XXX:
			 * Assumption is that files are well formed. No hasNext*() checks.
			 */
			
			// get dimensions
			int rows = inScanner.nextInt();
			int cols = inScanner.nextInt();
			inScanner.next(); // discard newline
			
			// create empty grid with dims
			PointElevation[][] grid = new PointElevation[rows][cols];
			
			// populate grid
			for (int i=0; i<rows; i++) {
				for (int j=0; j<cols; j++) {
					grid[i][j] = new PointElevation(inScanner.nextDouble());
				}
			}
			
			inScanner.close();
			return grid;
		}
		catch(FileNotFoundException e) { // very general exception handling
			System.out.println("Error opening or reading file "+filename);
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * <p>Writes basin data to output file with the given name.</p>
	 * <p>Note that if the file already exists, it must be empty</p>
	 * 
	 * @param total Total number of basins listed
	 * @param coords List of coordinates for each basin
	 * @param filename Name of output file
	 */
	public static void compileTerrainData(int total, int[][] coords, String filename) throws IOException {
		try {
			File outFile = new File(filename);
			
			// create file if it doesn't exist
			if (outFile.createNewFile()) {
				System.out.println("File "+filename+" created.");
			}
			else {
				System.out.println("File "+filename+" found.");
				if (outFile.length() != 0L) { // check if file is empty
					System.out.println("File "+filename+" not empty. Delete contents and "
							+ "rerun application.");
				}
			}
			
			FileWriter outWriter = new FileWriter(filename);
			
			// write total and coords to file
			outWriter.write(total + "\n");
			for (int i=0; i<coords.length; i++) {
				outWriter.write(coords[i][0] + " " + coords[i][1]);
			}
			
			outWriter.close();
		}
		catch (IOException e) { // very general exception handling
			e.printStackTrace();
		}
	}
}