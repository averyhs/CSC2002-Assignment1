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
	private double elevation;
	private boolean isBasin;
	private boolean isNotBasin;
	
	PointElevation(double elevation) {
		this.isBasin = false;
		this.isNotBasin = false;
		this.elevation = elevation;
	}
	
	public double val() {
		return elevation;
	}
	
	public void flagAsBasin() {
		isBasin = true;
	}
	
	public void flagAsNotBasin() {
		isNotBasin = true;
	}
	
	public boolean isBasin() {
		return isBasin;
	}
	
	public boolean isNotBasin() {
		return isNotBasin;
	}
	
	public String toString() {
		return String.valueOf(elevation);
	}
}