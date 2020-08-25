import java.util.concurrent.RecursiveTask;

/**
 * <p>Performs analysis on a grid of {@link PointElevation} objects. The essential
 * functionality is identifying all basins in the data, and collecting their coordinates 
 * into a list.</p>
 * 
 * <p>The identification of basins is done by the <code>findBasins()</code> method. 
 * <code>findBasins()</code> is written sequentially, and can be used to run the basin 
 * identification process in parallel by calling the <code>compute()</code> method. 
 * This is a method overridden from <code>RecursiveTask</code>.</p>
 * 
 * <p>Basins: the working definition of a basin in this package is a point whose 
 * neighbors all have greater values, which is interpreted as a point on a terrain 
 * where water may accumulate.</p>
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
	 * <p>Height difference threshold between a point and its 
	 * neighbors for the point to be classified as a basin.</p>
	 */
	private static final float HEIGHT_DIFF = 0.01f;
	
	/**
	 * <p>The cutoff for amount of data points processed in 
	 * parallel. Above this point, <code>compute()</code> method 
	 * continues recursing and creating new threads. Below this 
	 * point, <code>compute()</code> calls <code>findBasins()</code>
	 * to process the data, sequentially.</p>
	 */
	private static int SequentialCutoff = 500;
	
	/**
	 * <p>A grid of point elevation data to be analyzed,
	 *  transformed into 1 dimension.</p>
	 * <p>This is a static variable so that when new threads 
	 * are created with new instances of the class, all instances
	 * can access the map data without needing lots of copying and
	 * passing in as constructor arguments. <p> 
	 * <p>Being static does create some weak points though, for example 
	 * the case where a constructor that sets the map is called while some 
	 * of these instances are still supposed to be working on the 
	 * previous map. But since this is only written for one specific use, 
	 * it's not really a problem.</p>
	 */
	private static PointElevation[] map = null;
	
	/**
	 * <p>The number of columns of the original grid, needed
	 * for transforming between 1D indexes and 2D indexes.</p>
	 */
	private static int cols;
	
	/**
	 * <p>Index defining the start of the part of the map to be analyzed.</p>
	 */
	private int ilo;
	
	/**
	 * <p>Index defining the end of the part of the map to be analyzed.</p>
	 */
	private int ihi;
	
	/**
	 * <p>Creates a new <code>ElevationAnalysis</code> object with map 
	 * data given by the <code>PointElevation</code> array passed in. Indexes are 
	 * initialized to cover entire array.</p>
	 * 
	 * <p>Note: This constructor resets the static map of the class. 
	 * It should not be called while another instantiation is 
	 * still being used, or will be used, with the current map.</p>
	 * 
	 * @param m Array containing the data.
	 * @param c Number of columns of the data grid
	 */
	ElevationAnalysis(PointElevation[] m, int c) {
		map = m;
		cols = c;
		ilo = 0;
		ihi = map.length;
	}
	
	/**
	 * <p>Creates new <code>ElevationAnalysis</code> object with indexes set to
	 * cover the whole grid. Map is unchanged.</p>
	 */
	ElevationAnalysis() {
		ilo = 0;
		ihi = map.length;
	}
	
	/**
	 * <p>Creates a new <code>ElevationAnalysis</code> object with loop indexes 
	 * given by the values passed in. Map is unchanged.</p>
	 * 
	 * @param ilo Starting index
	 * @param ihi Ending index
	 */
	ElevationAnalysis(int ilo, int ihi) {
		this.ilo = ilo;
		this.ihi = ihi;
	}
	
	/**
	 * <p>Checks that all neighbors of the point at map index 
	 * <code>i</code> are at least <code>HEIGHT_DIFF</code> meters 
	 * higher</p>
	 * @param i Index of the point to check
	 * @return <code>true</code> if all neighbors are higher, <code>false</code> otherwise
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
	 * <p>Iterates through the part of the map defined by the 
	 * index fields of the object and flags points that meet basin 
	 * criteria.</p>
	 * 
	 * @return Number of basins found
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
	 * is used to call <code>findBasins()</code> on small bits of the 
	 * map in different threads.</p>
	 * 
	 * @return The number of basins in the data
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
