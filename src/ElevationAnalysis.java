import java.util.concurrent.RecursiveTask;

/**
 * <p>Perform analysis on a grid of {@link PointElevation} objects.</p>
 * 
 * <p>Finds basins in the grid data (A basin is defined as a point whose 
 * neighbors all have greater values, interpreted as a point on a terrain 
 * where water may accumulate.)</p>
 * 
 * <p>Parallel implementation:<br>
 * Uses divide-and-conquer algorithm on the PointElevation array.</p>
 * 
 * @author hrrhan002
 * 
 */
public class ElevationAnalysis extends RecursiveTask<Integer> {
	
	/* XXX:
	 * Not sure about this. I've let eclipse add a generated
	 * serialVersionUID because the documentation says not having it
	 * can result in unexpected InvalidClassExceptions due to 
	 * sensitivity to different compilers.
	 * 
	 * stackoverflow:
	 * https://stackoverflow.com/questions/285793/what-is-a-serialversionuid-and-why-should-i-use-it
	 */
	private static final long serialVersionUID = -6184225960884672569L;

	private static final float HEIGHT_DIFF = 0.01f; // threshold for basin
	private static final int SEQUENTIAL_CUTOFF = 500;
	/* TODO:
	 * Test to find best value for SEQUENTIAL_CUTOFF
	 */
	
	/* NOTE:
	 * map is static so that different threads can modify the points
	 * (simultaneously, in different places)
	 */
	private static PointElevation[][] map = null; // grid of elevation data
	
	private int basinCount; // counter for number of basins
	private int[] idxs; // indexes for traversing map: ilo, jlo, ihi, jhi
	
	/**
	 * <p>Creates new ElevationAnalysis object with basinCount set to 0 and
	 * indexes as specified.</p>
	 * 
	 * <p>This constructor does not initialize the map variable so be sure
	 * to use setmap() if there are no existing ElevationAnalysis objects
	 * when this constructor is called.</p>
	 */
	ElevationAnalysis(int ilo, int jlo, int ihi, int jhi) {
		this.basinCount = 0;
		idxs = new int[]{ilo, jlo, ihi, jhi};
	}
	
	/**
	 * <p>This constructor resets the static map of the class. It should only
	 * be called once for a particular data set. It should not be used while 
	 * other instantiations of ElevationAnalysis exist.</p>
	 * 
	 * <p>basinCount set to 0, index array set to null.</p>
	 * 
	 * @param map The terrain data in the format of a PointElevation array
	 */
	ElevationAnalysis(PointElevation[][] map) {
		ElevationAnalysis.map = map;
		System.out.println("Warning: ElevationAnalysis.map has been reset.");
		basinCount = 0;
		idxs = null;
	}
	
	/**
	 * <p>Iterates through the section of the PointElevation array 
	 * specified by the index parameters and flags points that meet 
	 * the basin criteria.</p>
	 * 
	 * <p>Note that borders are classified as notBasin</p>
	 * 
	 * @param ilo The starting row index
	 * @param jlo The starting column index
	 * @param ihi The ending row index
	 * @param jhi The ending column index.
	 */
	public void findBasins(int ilo, int jlo, int ihi, int jhi) {	
		for (int i=ilo; i<ihi; i++) { // iterate through rows
			for (int j=jlo; j<jhi; j++) { // iterate through columns
				
				if (i==0 || i==map.length || j==0 || j==map[0].length) {
					// this point is on the border of the map
					continue;
				}
				
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
	 * <p>Overloaded findBasins method. This can be called to process the 
	 * entire map sequentially.</p>
	 */
	public void findBasins() {
		/* NOTE:
		 * Could exclude borders from the for loop here but i want the
		 * parallel execution and sequential execution to process the
		 * same number of points, for comparison.
		 */
		for (int i=0; i<map.length; i++) { // iterate through rows
			for (int j=0; j<map[0].length; j++) { // iterate through columns
				
				if (i==0 || i==map.length || j==0 || j==map[0].length) {
					// this point is on the border of the map
					continue;
				}
				
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
		
		// for loop indexes exclude borders (automatically NotBasin)
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
	
	public Integer compute() {
		int num_rows = idxs[2]-idxs[0];
		int num_cols = idxs[3]-idxs[1];
		if ( (num_rows*num_cols) < SEQUENTIAL_CUTOFF) {
			findBasins(idxs[0], idxs[1], idxs[2], idxs[3]);
			return basinCount;
		}
		
		else {
			// decide how to divide portion of map marked by idxs
			int i_cut, j_cut;
			if (num_rows >= num_cols) {
				i_cut = idxs[2]/2;
				j_cut = idxs[3];
			}
			else {
				i_cut = idxs[2];
				j_cut = idxs[3]/2;
			}
			
			// spawn branches
			ElevationAnalysis left = new ElevationAnalysis(idxs[0], idxs[1], i_cut, j_cut);
			ElevationAnalysis right = new ElevationAnalysis(i_cut, j_cut, idxs[0], idxs[1]);
			
			left.fork();
			int rightnum = right.compute();
			int leftnum = left.join();
			
			return leftnum+rightnum;
		}
		
	}
	
	/**
	 * Accessor
	 * @return number of basins found in data
	 */
	public int basinCount() {
		return basinCount;
	}
	
	/**
	 * <p>(Re)sets static map. Use with caution.</p>
	 * @param m PointElevation array to assign to ElevationAnalysis.map
	 */
	public void setmap(PointElevation[][] m) {
		ElevationAnalysis.map = m;
		System.out.println("Warning: ElevationAnalysis.map has been reset.");
	}
}



