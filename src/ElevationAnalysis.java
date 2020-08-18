import java.util.concurrent.RecursiveTask;

/**
 * <p>Perform analysis on a grid of {@link PointElevation} objects.</p>
 * 
 * <p>Finds basins in the grid data (A basin is defined as a point whose 
 * neighbors all have greater values, interpreted as a point on a terrain 
 * where water may accumulate.)</p>
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
	private static final long serialVersionUID = -4191663543839944617L;

	/**
	 * <p>Height difference threshold, between a point and its 
	 * neighbors, for point to be classified as a basin.</p>
	 */
	private static final float HEIGHT_DIFF = 0.01f;
	
	/**
	 * <p>The cutoff for amount of data points processed in 
	 * parallel. Below this point, processing is sequential.</p>
	 */
	private static final int SEQUENTIAL_CUTOFF = 500;
	
	/**
	 * <p>A grid of point elevations, the data to be analyzed.</p>
	 * <p>This is a static variable so that all the threads of my 
	 * simple divide-and-conquer algorithm can access it. Being
	 * static does create some weak points though, for example 
	 * if a constructor that sets the map is called while some 
	 * of these objects are still supposed to be working on the 
	 * previous map.</p>
	 */
	private static PointElevation[][] map = null;
	
	int ilo, jlo, ihi, jhi; // indexes for for loops
	int basinCount;
	
	/**
	 * <p>Creates a new ElevationAnalysis object with map data given
	 * by the PointElevation array passed in.</p>
	 * 
	 * <p>Note: This constructor resets the static map of the class. 
	 * It should not be called while another instantiation is 
	 * still being, or will still be, used.</p>
	 * 
	 * @param m A point elevation grid containing the data.
	 */
	ElevationAnalysis(PointElevation[][] m) {
		map = m;
		System.out.println("Warning: ElevationAnalysis.map has been reset.");
		
		basinCount = 0;
	}
	
	/**
	 * <p>Creates a new ElevationAnalysis object with loop indexes 
	 * given by the values passed in. Map is unchanged.</p>
	 * 
	 * @param ilo Starting row index
	 * @param jlo Starting col index
	 * @param ihi Ending row index
	 * @param jhi Ending col index
	 */
	ElevationAnalysis(int ilo, int jlo, int ihi, int jhi) {
		basinCount = 0;
		
		this.ilo = ilo;
		this.jlo = jlo;
		this.ihi = ihi;
		this.jhi = jhi;
	}
	
	/*
	 * Check all neighbors of point(i,j) are at least 
	 * HEIGHT_DIFF meters higher
	 */
	private boolean passBasinCheck(int i, int j) {
		boolean pass = 
				map[i][j].val()+HEIGHT_DIFF <= map[i-1][j-1].val() &&
				map[i][j].val()+HEIGHT_DIFF <= map[i-1][j].val() &&
				map[i][j].val()+HEIGHT_DIFF <= map[i-1][j+1].val() &&
				map[i][j].val()+HEIGHT_DIFF <= map[i][j-1].val() &&
				map[i][j].val()+HEIGHT_DIFF <= map[i][j+1].val() &&
				map[i][j].val()+HEIGHT_DIFF <= map[i+1][j-1].val() &&
				map[i][j].val()+HEIGHT_DIFF <= map[i+1][j].val() &&
				map[i][j].val()+HEIGHT_DIFF <= map[i+1][j+1].val();
		 
		return pass;
	}
	
	/**
	 * <p>Iterates through the part of the map specified by the
	 * object's indexes and flags points that meet basin criteria.</p>
	 */
	public void findBasins() {
		for (int i=ilo; i<ihi; i++) {
			for (int j=jlo; j<jhi; j++) {
				
				if (i==0 || i==map.length || j==0 || j==map[0].length) {
					// point is on the border of the map
					continue;
				}
				
				if (passBasinCheck(i,j)) {
					// point qualifies as basin
					map[i][j].flagAsBasin();
					basinCount++;
				}
			}
		}
	}
	
	/**
	 * <p>Collates flagged basins into an array containing the coords
	 * of each basin.</p>
	 * 
	 * @return List of basin coords
	 */
	public int[][] listBasins() {
		int[][] list = new int[basinCount][2];
		int l=0; // list index
		
		for (int i=0; i<map.length; i++) {
			for (int j=0; j<map.length; j++) {
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
	 * <p>Finds basins in parallel. A divide-and-conquer algorithm
	 * is used to perform findBasins() on small bits of the map in
	 * parallel.</p>
	 */
	@Override
	public Integer compute() {
		int num_rows = ihi-ilo;
		int num_cols = jhi-jlo;
		
		if ((num_rows*num_cols) < SEQUENTIAL_CUTOFF) {
			findBasins(); // do sequentially
			return basinCount;
		}
		
		else {
			// Spawn branches
			ElevationAnalysis b1;
			ElevationAnalysis b2;
			
			// divide portion of map by halving longest 'side'
			if (num_rows >= num_cols) {
				b1 = new ElevationAnalysis(ilo, jlo, (ilo+ihi)/2, jhi);
				b2 = new ElevationAnalysis((ilo+ihi)/2, jlo, ihi, jhi); 
			}
			else { // num_cols > num_rows
				b1 = new ElevationAnalysis(ilo, jlo, ihi, (jlo+jhi)/2);
				b2 = new ElevationAnalysis(ilo, (jlo+jhi)/2, ihi, jhi);
			}
			
			b1.fork();
			int b2Ans = b2.compute();
			int b1Ans = b1.join();
			
			return b1Ans+b2Ans;
		}
	}
	
	/**
	 * <p>Accessor for basinCount.</p>
	 * @return Number of basins found in data
	 */
	public int basinCount() {
		return basinCount;
	}
	
	/**
	 * <p>Clears flags on all points in the map.</p> 
	 */
	public void clearFlags() {
		for (int i=0; i<map.length; i++) {
			for (int j=0; j<map[0].length; j++) {
				map[i][j].clearFlag();
			}
		}
	}
	
}











