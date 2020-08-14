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
	private PointElevation[][] map;
	
	ElevationAnalysis(PointElevation[][] map) {
		this.map = map;
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
	 * </ul></p>
	 */
	public void findBasins() {
		for (int i=0; i<map.length; i++) { // iterate through rows
			for (int j=0; j<map[i].length; j++) { // iterate through columns
				
				if (map[i][j].isBasin() || map[i][j].isNotBasin()) {
					continue; // skip if point is already classified
				}
				
				else { // unclassified point
					if (map[i][j].val()+0.01 <= map[i-1][j-1].val() &&
						map[i][j].val()+0.01 <= map[i-1][j].val() &&
						map[i][j].val()+0.01 <= map[i-1][j+1].val() &&
						map[i][j].val()+0.01 <= map[i][j-1].val() &&
						map[i][j].val()+0.01 <= map[i][j+1].val() &&
						map[i][j].val()+0.01 <= map[i+1][j-1].val() &&
						map[i][j].val()+0.01 <= map[i+1][j].val() &&
						map[i][j].val()+0.01 <= map[i+1][j+1].val()) {
						
						// all neighbors are at least 0.01m higher
						// ==> qualifies as basin
						map[i][j].flagAsBasin();
						
						// also, neighbors qualify as not-basins
						/* XXX:
						 * Is it more efficient to do checks first
						 * or just (re)assign?
						 * or even just leave this out.
						*/
						/* XXX:
						 * Could this introduce race conditions
						 * in parallel implementation?
						 */
						map[i-1][j-1].flagAsNotBasin();
						map[i-1][j].flagAsNotBasin();
						map[i-1][j+1].flagAsNotBasin();
						map[i][j-1].flagAsNotBasin();
						map[i][j+1].flagAsNotBasin();
						map[i+1][j-1].flagAsNotBasin();
						map[i+1][j].flagAsNotBasin();
						map[i+1][j+1].flagAsNotBasin();
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
	
	public int[][] listBasins() {}
}