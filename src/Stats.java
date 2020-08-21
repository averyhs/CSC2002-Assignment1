/**
 * <p>Basic statistical functions (min, max, averages) for arrays</p>
 * 
 * @author avk
 *
 */
public class Stats {
	/**
	 * <p>Finds highest valued element in array.</p>
	 * @param arr Array
	 * @return maximum
	 */
	public static double max(long[] arr) {
		double max = 0;
		for (int i=0; i<arr.length; i++) {
			if (arr[i]>max) {
				max = arr[i];
			}
		}
		return max;
	}
	
	/**
	 * <p>Finds lowest valued element in array.</p>
	 * @param arr Array
	 * @return minimum
	 */
	public static double min(double[] arr) {
		double min = 0;
		for (int i=0; i<arr.length; i++) {
			if (arr[i]>min) {
				min = arr[i];
			}
		}
		return min;
	}
	
	/**
	 * <p> Calculates sum of all elements in array.</p>
	 * @param arr Array
	 * @return sum
	 */
	public static double sum(double[] arr) {
		double sum = 0;
		for (int i=0; i<arr.length; i++) {
			sum += arr[i];
		}
		return sum;
	}
	
}