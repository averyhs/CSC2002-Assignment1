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
	private static long t_tick;
	
	public static void main(String[] args) {
		// variables & storage arrays for speed tests:
		int n = 20; // number of times to run speed tests
		int p = 5; // number of sequential cutoffs to test at
		long[][] seqTimes = new long[p][n];
		long[][] parTimes = new long[p][n];
		int[] cutoffs = new int[p];
		
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
			ElevationAnalysis.setSequentialCutoff((int)(5*Math.pow(10,c)));
			cutoffs[c] = (int)(5*Math.pow(10,c));
			for (int i=0; i<n; i++) { // run n tests
				ElevationAnalysis.clearFlags();
				tick();
				analyze.findBasins(); // sequential
				seqTimes[c][i] = tock();
				ElevationAnalysis.clearFlags();
				tick();
				fjPool.invoke(new ElevationAnalysis()); // parallel
				parTimes[c][i] = tock();
			}
		}
		
		String dataSize = String.valueOf(ElevationAnalysis.getMapdims()[0])+"x"+String.valueOf(ElevationAnalysis.getMapdims()[1]);
		MyFiles.compileTestData(seqTimes, cutoffs, dataSize, "sequential");
		MyFiles.compileTestData(parTimes, cutoffs, dataSize, "parallel");
	}
	
	/**
	 * <p>Records the current time (stored in static field).</p>
	 */
	private static void tick() {
		t_tick = System.nanoTime();
	}
	
	/**
	 * <p>Calculates and returns the time elapsed since 
	 * the last tick() call.</p>
	 * @return Elapsed time in ms
	 */
	private static long tock() {
		return System.nanoTime()-t_tick;
	}
}