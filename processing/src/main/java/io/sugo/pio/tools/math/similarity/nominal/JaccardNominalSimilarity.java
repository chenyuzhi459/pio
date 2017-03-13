package io.sugo.pio.tools.math.similarity.nominal;

/**
 * Implements the Dice similarity for nominal attributes.
 * 
 */
public class JaccardNominalSimilarity extends AbstractNominalSimilarity {

	private static final long serialVersionUID = 6210763597892503744L;

	@Override
	protected double calculateSimilarity(double equalNonFalseValues, double unequalValues, double falseValues) {
		return equalNonFalseValues / (equalNonFalseValues + unequalValues);
	}

	@Override
	public String toString() {
		return "Jaccard nominal similarity";
	}
}
