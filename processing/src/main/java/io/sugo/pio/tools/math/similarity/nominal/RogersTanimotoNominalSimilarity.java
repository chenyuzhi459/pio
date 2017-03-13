package io.sugo.pio.tools.math.similarity.nominal;

/**
 * Implements the Rogers-Tanimoto similarity for nominal attributes.
 * 
 */
public class RogersTanimotoNominalSimilarity extends AbstractNominalSimilarity {

	private static final long serialVersionUID = -9171250400618365198L;

	@Override
	protected double calculateSimilarity(double equalNonFalseValues, double unequalValues, double falseValues) {
		return (equalNonFalseValues + falseValues) / (equalNonFalseValues + falseValues + 2 * unequalValues);
	}

	@Override
	public String toString() {
		return "Rogers-Tanimoto nominal similarity";
	}

}
