import java.util.concurrent.ForkJoinPool;

/**
 * <p>Contains <code>main</code> method to interface with user and 
 * implement functionality of {@link ElevationAnalysis} class.</p>
 * 
 * @author hrrhan002
 *
 */
public class TerrainClassify {
	private static ElevationAnalysis analyze; // redundant for parallel, needed for sequential
	private static ForkJoinPool fjPool = new ForkJoinPool();
	private static double t_tick;
	
	public static void main(String[] args) {
		// variables & storage arrays for speed tests:
		int n = 20; // number of times to run speed tests
		int p = 5; // number of sequential cutoffs to test at
		double[][] seq_times = new double[p][n];
		double[][] par_times = new double[p][n];
		
		String infile = args[0];
		analyze = new ElevationAnalysis(MyFiles.extractTerrainData(infile));
		
		/*
		 * Test 20 times for each sequential cutoff (~ number of threads)
		 * 
		 * Coarse version:
		 * seq cutoff increases in orders of magnitude
		 * 
		 * Fine version:
		 * seq cutoff increases by constant step
		 */
		for (int c=0; c<p; c++) { // increment cutoff p times
			ElevationAnalysis.setSequentialCutoff((int)(5*Math.pow(10,p)));
			for (int i=0; i<n; i++) { // run n tests
				ElevationAnalysis.clearFlags();
				tick();
				analyze.findBasins(); // sequential
				seq_times[c][i] = tock();
				ElevationAnalysis.clearFlags();
				tick();
				fjPool.invoke(new ElevationAnalysis()); // parallel
				par_times[c][i] = tock();
			}
		}
	}
	
	/**
	 * <p>Records the current time (stored in static field)</p>
	 */
	private static void tick() {
		t_tick = System.currentTimeMillis();
	}
	
	/**
	 * <p>Calculates and returns the time elapsed since 
	 * the last tick() call</p>
	 * @return Elapsed time in ms
	 */
	private static double tock() {
		return System.currentTimeMillis() - t_tick;
	}
}