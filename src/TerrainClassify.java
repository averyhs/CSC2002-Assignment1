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
		for (int p=0; p<=4; p++) { // increment power of 10
			ElevationAnalysis.setSequentialCutoff((int)(5*Math.pow(10,p)));
			for (int n=0; n<20; n++) { // run 20 tests
				ElevationAnalysis.clearFlags();
				tick();
				analyze.findBasins(); // sequential
				tock();
				ElevationAnalysis.clearFlags();
				tick();
				fjPool.invoke(new ElevationAnalysis()); // parallel
				tock();
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