import java.util.concurrent.ForkJoinPool;

/**
 * <p>Contains <code>main</code> method to interface with user and 
 * implement functionality of {@link ElevationAnalysis} class.</p>
 * 
 * @author hrrhan002
 *
 */
public class TerrainClassify {
	/**
	 * <p>An <code>ElevationAnalysis</code> object created 
	 * with the data from the file given as input to <code>main()</code>.</p>
	 */
	private static ElevationAnalysis analyze;
	
	/**
	 * <p>A <code>ForkJoinPool</code> object to handle threads.</p>
	 */
	private static ForkJoinPool fjPool = new ForkJoinPool();
	
	/**
	 * <p>Start time of a benchmark test. Value set by <code>tick()</code>
	 * for temporary storage until <code>tock()</code> is called.</p>
	 */
	private static long t_tick;
	
	/**
	 * <p>Reads input file, identifies all basins in the data, writes their
	 * coordinates to output file, and performs benchmarking tests to compare
	 * parallel vs sequential processing of the data.</p>
	 * <p>The path of the data file is given as the first argument to the method, 
	 * and the path of the file to write to is given as the second argument on
	 * command line. A third argument may be given, "-b". If that argument is
	 * given, benchmarking will be performed, otherwise benchmarking will be 
	 * skipped and all the method will do is identify the basins (in parallel)
	 * and write to the output file.</p>
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// IO files
		String infile = args[0];
		String outfile = args[1];
		
		analyze = new ElevationAnalysis(MyFiles.extractTerrainData(infile), MyFiles.getDataDims()[1]);
		
		if (args.length>2) {
			if (args[2].equals("-b")) {
				// variables & storage arrays for speed tests:
				int n = 20; // number of times to run speed tests
				int p = 7; // number of sequential cutoffs to test at
				double[][] seqTimes = new double[p][n];
				double[][] parTimes = new double[p][n];
				int[] cutoffs = new int[p];
				
				// 'warm-up'
				for (int i=0; i<100; i++) { // num loops based on experimentation
					fjPool.invoke(new ElevationAnalysis());
				}
				
				/*
				 * Test 20 times for each sequential cutoff (~ number of threads)
				 * 
				 * Coarse version:
				 * seq cutoff increases in orders of magnitude
				 * 
				 * Fine version:
				 * seq cutoff increases by constant step
				 */				
				// coarse
				System.out.println("Doing benchamrk test coarse...");
				for (int c=0; c<p; c++) { // increment cutoff p times
					ElevationAnalysis.setSequentialCutoff((int)(5*Math.pow(10,c)));
					cutoffs[c] = (int)(5*Math.pow(10,c));
					for (int i=0; i<n; i++) { // run n tests
						System.gc(); // minimize chances of gc running in timing blocks
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
				System.out.println("Writing to file...");
				MyFiles.compileTestData(seqTimes, parTimes, cutoffs, "coarse", true);
				
				// fine
				System.out.println("Doing benchamrk test fine...");
				for (int c=0; c<p; c++) { // increment cutoff p times
					ElevationAnalysis.setSequentialCutoff(250+c*2500/(p));
					cutoffs[c] = (int)(250+c*2500/(p));
					for (int i=0; i<n; i++) { // run n tests
						System.gc(); // minimize chances of gc running in timing blocks
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
				System.out.println("Writing to file...");
				MyFiles.compileTestData(seqTimes, parTimes, cutoffs, "fine", true);
				
				ElevationAnalysis.clearFlags();
			}
		}
		
		// Produce list of basin coords
		System.out.println("Finding basins...");
		int num_basins = fjPool.invoke(new ElevationAnalysis());
		System.out.println("Writing to file...");
		MyFiles.compileTerrainData(num_basins, ElevationAnalysis.listBasins(num_basins), outfile);
	}
	
	/**
	 * <p>Records the current time (stored in <code>t_tick</code> field).</p>
	 */
	private static void tick() {
		t_tick = System.nanoTime();
	}
	
	/**
	 * <p>Calculates and returns the time elapsed since the last tick() call.</p>
	 * 
	 * @return Elapsed time in ms
	 */
	private static double tock() {
		return (System.nanoTime()-t_tick)*Math.pow(10,-6);
	}
	
}