import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * File handling functions for Assignment1
 * 
 * @author hrrhan002
 *
 */
class MyFiles {
	public static PointElevation[][] extractTerrainData(String filename) {}
	
	/**
	 * <p>Writes basin data to output file with the given name.</p>
	 * <p>Note that if the file already exists, it must be empty-</p>
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
					throw new IOException("File "+filename+" not empty. Delete contents and "
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