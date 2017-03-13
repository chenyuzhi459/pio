package io.sugo.pio.tools.math.similarity.nominal;

/**
 * Implements the simple matching similarity for nominal attributes.
 * 
 */
public class SimpleMatchingNominalSimilarity extends AbstractNominalSimilarity {

	private static final long serialVersionUID = -2638243053923231698L;

	@Override
	protected double calculateSimilarity(double equalNonFalseValues, double unequalValues, double falseValues) {
		return (equalNonFalseValues + falseValues) / (equalNonFalseValues + unequalValues + falseValues);
	}

	@Override
	public String toString() {
		return "Matching nominal similarity";
	}

}
