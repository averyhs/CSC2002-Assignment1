/**
 * <p>Container for float representing the elevation of a point on terrain. 
 * Allows for flagging of point as Basin.</p>
 * 
 * @author hrrhan002
 *
 */
public class PointElevation {
	private float elevation;
	private boolean isBasin;
	
	PointElevation(float elevation) {
		this.isBasin = false;
		this.elevation = elevation;
	}
	
	public float val() {
		return elevation;
	}
	
	public void flagAsBasin() {
		isBasin = true;
	}
	
	public boolean isBasin() {
		return isBasin;
	}
	
	public String toString() {
		return String.valueOf(elevation);
	}
}