/**
 * <p>Contains <code>main</code> method to interface with user and 
 * implement functionality of {@link ElevationAnalysis} class.</p>
 * 
 * @author hrrhan002
 *
 */
public class TerrainClassify {
	private static ElevationAnalysis analyze;
	private static double t_tick;
	
	public static void main(String[] args) {
		System.out.println("This is the first debug.");
		
		String ipp = "../io-files/"; // input file path prefix
		String opp = "../io-files/"; // output file path prefix
		
		String infile = ipp+args[0];
		String outfile = opp+args[1];
		
		analyze = new ElevationAnalysis(MyFiles.extractTerrainData(infile));
		analyze.findBasins();
		MyFiles.compileTerrainData(analyze.basinCount(), analyze.listBasins(), outfile);
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