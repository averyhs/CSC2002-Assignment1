import java.util.concurrent.RecursiveTask;

/**
 * <p>Perform analysis on a grid of {@link PointElevation} objects.</p>
 * 
 * <p>Finds basins in the grid data (A basin is defined as a point whose 
 * neighbors all have greater values, interpreted as a point on a terrain 
 * where water may accumulate.)</p>
 * 
 * <p>Parallel implementation:<br>
 * Uses divide-and-conquer algorithm on the PointElevation array. Note that
 * RecursiveTask has type int[][]; Nothing needs to be returned when only 
 * findBasins() is done in parallel, so the type is set to int[][] for a 
 * possible extension to listBasins() too.</p>
 * 
 * @author hrrhan002
 * 
 */
public class ElevationAnalysis extends RecursiveTask<int[][]> {
	private static final float HEIGHT_DIFF = 0.01f; // threshold for basin
	private static final int SEQUENTIAL_CUTOFF = 500;
	
	/* TODO:
	 * Test to find best value for SEQUENTIAL_CUTOFF
	 */
	
	private PointElevation[][] map; // grid of elevation data
	private int basinCount; // counter for number of basins	
	private boolean inParallel; // run parallel or sequential
	
	ElevationAnalysis(PointElevation[][] map, boolean inParallel) {
		this.map = map;
		this.basinCount = 0;
		this.inParallel = inParallel;
	}
	
	/**
	 * <p>Iterates through the PointElevation array and flags points that
	 * meet the basin criteria.</p>
	 */
	public void findBasins(int ilo, int jlo, int ihi, int jhi) {
				
		for (int i=ilo; i<ihi; i++) { // iterate through rows
			for (int j=jlo; j<jhi; j++) { // iterate through columns
				
				if (map[i][j].val()+HEIGHT_DIFF <= map[i-1][j-1].val() &&
						map[i][j].val()+HEIGHT_DIFF <= map[i-1][j].val() &&
						map[i][j].val()+HEIGHT_DIFF <= map[i-1][j+1].val() &&
						map[i][j].val()+HEIGHT_DIFF <= map[i][j-1].val() &&
						map[i][j].val()+HEIGHT_DIFF <= map[i][j+1].val() &&
						map[i][j].val()+HEIGHT_DIFF <= map[i+1][j-1].val() &&
						map[i][j].val()+HEIGHT_DIFF <= map[i+1][j].val() &&
						map[i][j].val()+HEIGHT_DIFF <= map[i+1][j+1].val()) {
					
					// all neighbors are at least 0.01m higher
					// ==> qualifies as basin
					map[i][j].flagAsBasin();
					basinCount++;
				}

			}
		}
	}
	
	/**
	 * Collates Basin/NotBasin data into an array containing the coords
	 * of each basin
	 * 
	 * @return List of basin coords
	 */
	public int[][] listBasins() {
		int[][] list = new int[basinCount][2];
		int l=0; // list index
		
		// for loop indices exclude borders (automatically NotBasin)
		int ilo=1, jlo=1;
		int ihi=map.length-1, jhi=map[0].length-1;
		
		for (int i=ilo; i<ihi; i++) { // iterate through rows
			for (int j=jlo; j<jhi; j++) { // iterate through columns
				if (map[i][j].isBasin()) {
					list[l][0] = i;
					list[l][1] = j;
					l++;
				}
			}
		}
		return list;
	}
	
	/**
	 * Accessor
	 * @return number of basins found in data
	 */
	public int basinCount() {
		return basinCount;
	}
	
	public int[][] compute() {
		
	}
}

// compute:
// loop indices for sequential computation:
	// for loop indices exclude borders (automatically NotBasin)
	// int ilo=1, jlo=1;
	// int ihi=map.length-1, jhi=map[0].length-1;





