package io.sugo.pio.tools.math.similarity;

/**
 * This empty interface only indicates if a measure is more primary a similarity measure than a
 * distance measure. Nevertheless both measure types have to implement both methods properly, so
 * this interface is only for programmers orientation NOT for testing with instanceof!
 * 
 */
public abstract class SimilarityMeasure extends DistanceMeasure {

	private static final long serialVersionUID = -2138479771882810015L;

	/**
	 * This method returns a boolean whether this measure is a distance measure
	 * 
	 * @return true if is distance
	 */
	@Override
	public final boolean isDistance() {
		return false;
	}
}
