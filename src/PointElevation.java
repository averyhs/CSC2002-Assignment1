
/**
 * <p>Data type for terrain elevation data. Encodes elevation and basin status
 * of a point, and provides functionality for accessing data and changing the 
 * basin status.</p>
 * <p>(See {@link ElevationAnalysis} for the working definition of a basin)</p>
 * 
 * @author hrrhan002
 *
 */
public class PointElevation {
	/**
	 * <p>Elevation of the point in meters above sea level</p>
	 */
	private float elevation;
	/**
	 * <p>Basin status of the point</p>
	 */
	private boolean isBasin;
	
	/**
	 * <p>Creates a new <code>PointElevation</code> object, with the given
	 * elevation and the basin status  set to <code>false</code></p>
	 * @param elevation The elevation of the point
	 */
	PointElevation(float elevation) {
		this.isBasin = false;
		this.elevation = elevation;
	}
	
	/**
	 * <p>Gets the elevation of the point</p>
	 * @return Elevation value
	 */
	public float val() {
		return elevation;
	}
	
	/**
	 * <p>Sets basin status of the point to <code>true</code></p>
	 */
	public void flagAsBasin() {
		isBasin = true;
	}
	
	/**
	 * <p>Sets basin status of the point to <code>false</code></p>
	 */
	public void clearFlag() {
		isBasin = false;
	}
	
	/**
	 * <p>Gets the basin status of the point </p>
	 * @return Basin status
	 */
	public boolean isBasin() {
		return isBasin;
	}
	
	/**
	 * <p>Gets the string representation of the elevation.</p>
	 * @return Elevation of the point, as a string
	 */
	@Override
	public String toString() {
		return String.valueOf(elevation);
	}
}