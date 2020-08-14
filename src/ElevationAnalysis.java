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
public class ElevationAnalysis {
	private static final double HEIGHT_DIFF = 0.01; // threshold
	
	private PointElevation[][] map; // grid of elevation data
	private int basinCount; // counter: number of basins	
	
	ElevationAnalysis(PointElevation[][] map) {
		this.map = map;
		this.basinCount = 0;
	}
	
	/**
	 * <p>Iterates through the PointElevation array and classifies each as
	 * Basin or NotBasin.</p>
	 * 
	 * <p>Some implementation details:
	 * <ul>
	 *   <li>When a point is classified as Basin, all neighbors are 
	 *   classified as NotBasin</li>
	 *   <li>Point is skipped if already classified</li>
	 *   <li>Borders are excluded</li>
	 * </ul></p>
	 */
	public void findBasins() {
		// for loop indices exclude borders (automatically NotBasin)
		int ilo=1, jlo=1;
		int ihi=map.length-1, jhi=map[0].length-1;
		
		for (int i=ilo; i<ihi; i++) { // iterate through rows
			for (int j=jlo; j<jhi; j++) { // iterate through columns
				
				if (map[i][j].isBasin() || map[i][j].isNotBasin()) {
					continue; // skip if point is already classified
				}
				
				else { // unclassified point
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
						
						// also, neighbors qualify as not-basins
						map[i-1][j-1].flagAsNotBasin();
						map[i-1][j].flagAsNotBasin();
						map[i-1][j+1].flagAsNotBasin();
						map[i][j-1].flagAsNotBasin();
						map[i][j+1].flagAsNotBasin();
						map[i+1][j-1].flagAsNotBasin();
						map[i+1][j].flagAsNotBasin();
						map[i+1][j+1].flagAsNotBasin();
						/* XXX:
						 * Is it more efficient to do checks first
						 * or just (re)assign?
						 * or even just leave this out.
						*/
						/* XXX:
						 * Could this introduce race conditions
						 * in parallel implementation?
						 */
					}
					
					else {
						// not all neighbors are at least 0.01m higher
						// ==> qualifies as not-basin
						map[i][j].flagAsNotBasin();
					}
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
}







