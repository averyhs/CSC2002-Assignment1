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
	private static int SequentialCutoff = 500;
	
	/**
	 * <p>A grid of point elevations, the data to be analyzed,
	 *  mapped onto a 1-dimensional array.</p>
	 * <p>This is a static variable so that all the threads of my 
	 * simple divide-and-conquer algorithm can access it. Being
	 * static does create some weak points though, for example 
	 * if a constructor that sets the map is called while some 
	 * of these objects are still supposed to be working on the 
	 * previous map.</p>
	 */
	private static PointElevation[] map = null;
	
	/**
	 * <p>The number of columns of the original grid, needed
	 * for transforming between 1D indexes and 2D indexes.</p>
	 */
	private static int cols;
	
	
	int ilo, ihi; // indexes for for loops
	
	/**
	 * <p>Creates a new ElevationAnalysis object with map data given
	 * by the PointElevation array passed in. Indexes initialized
	 * to cover entire array.</p>
	 * 
	 * <p>Note: This constructor resets the static map of the class. 
	 * It should not be called while another instantiation is 
	 * still being, or will still be, used.</p>
	 * 
	 * @param m A point elevation grid containing the data.
	 */
	ElevationAnalysis(PointElevation[] m, int c) {
		map = m;
		cols = c;
		ilo = 0;
		ihi = map.length;
		//System.out.println("Warning: ElevationAnalysis.map has been reset.");
	}
	
	/**
	 * <p>Creates new ElevationAnalysis object with loop indexes set to
	 * cover the whole grid. Map is unchanged.</p>
	 */
	ElevationAnalysis() {
		ilo = 0;
		ihi = map.length;
	}
	
	/**
	 * <p>Creates a new ElevationAnalysis object with loop indexes 
	 * given by the values passed in. Map is unchanged.</p>
	 * 
	 * @param ilo Starting index
	 * @param ihi Ending index
	 */
	ElevationAnalysis(int ilo, int ihi) {
		this.ilo = ilo;
		this.ihi = ihi;
	}
	
	/*
	 * Check all neighbors of point(i,j) are at least 
	 * HEIGHT_DIFF meters higher
	 */
	private boolean passBasinCheck(int i) {
		boolean pass = 
				map[i].val()+HEIGHT_DIFF <= map[i-cols-1].val() &&
				map[i].val()+HEIGHT_DIFF <= map[i-cols].val() &&
				map[i].val()+HEIGHT_DIFF <= map[i-cols+1].val() &&
				map[i].val()+HEIGHT_DIFF <= map[i-1].val() &&
				map[i].val()+HEIGHT_DIFF <= map[i+1].val() &&
				map[i].val()+HEIGHT_DIFF <= map[i+cols-1].val() &&
				map[i].val()+HEIGHT_DIFF <= map[i+cols].val() &&
				map[i].val()+HEIGHT_DIFF <= map[i+cols+1].val();
		return pass;
	}
	
	/**
	 * <p>Iterates through the part of the map specified by the
	 * object's indexes and flags points that meet basin criteria.</p>
	 */
	public int findBasins() {
		int basinCount = 0;
		for (int i=ilo; i<ihi; i++) {
			if (i<cols || i>(map.length-cols) || (i%cols)==0 || (i%cols)==(cols-1)) {
				// point is on the border of the map
				continue;
			}
			if (passBasinCheck(i)) {
				// point qualifies as basin
				map[i].flagAsBasin();
				basinCount++;
			}
		}
		return basinCount;
	}
	
	/**
	 * <p>Collates flagged basins into an array containing the coords
	 * of each basin.</p>
	 * 
	 * @param basinCount Number of basins
	 * @return List of basin coords
	 */
	public static int[][] listBasins(int basinCount) {
		int[][] list = new int[basinCount][2];
		int l=0; // list index
		
		for (int i=0; i<map.length; i++) {
			if (map[i].isBasin()) {
				list[l][0] = i/cols;
				list[l][1] = i%cols;
				l++;
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
		if ((ihi-ilo) < SequentialCutoff) {
			return findBasins(); // do sequentially
		}
		
		else {
			// Spawn branches
			ElevationAnalysis b1 = new ElevationAnalysis(ilo, (ilo+ihi)/2);
			ElevationAnalysis b2 = new ElevationAnalysis((ilo+ihi)/2, ihi);
			
			b1.fork();
			int b2Ans = b2.compute();
			int b1Ans = b1.join();
			
			return b1Ans+b2Ans;
		}
	}
	
	/**
	 * <p>Sets the sequential cutoff value for the class.</p>
	 * 
	 * @param cutoff New sequential cutoff value
	 */
	public static void setSequentialCutoff(int cutoff) {
		SequentialCutoff = cutoff;
	}
	
	/**
	 * <p>Clears flags on all points in the map.</p> 
	 */
	public static void clearFlags() {
		for (int i=0; i<map.length; i++) {
			map[i].clearFlag();
		}
	}
	
}
