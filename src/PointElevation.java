/**
 * <p>Container for float representing the elevation of a point on terrain. 
 * Allows for flagging of point as Basin or NotBasin.</p>
 * 
 * <p>Note that flags are <code>false</code> when condition is false or
 * unclassified. Only a value of <code>true</code> has a definite meaning.
 * 
 * @author hrrhan002
 *
 */
public class PointElevation {
	private float elevation;
	private boolean isBasin;
	private boolean isNotBasin;
	
	PointElevation() {}
	
	public void flagAsBasin() {}
	
	public void flagAsNotBasin() {}
	
	public boolean isBasin() {}
	
	public boolean isNotBasin() {}
}