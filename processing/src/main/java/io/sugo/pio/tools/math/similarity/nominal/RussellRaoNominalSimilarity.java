package io.sugo.pio.tools.math.similarity.nominal;

/**
 * Implements the Russell-Rao similarity for nominal attributes.
 * 
 */
public class RussellRaoNominalSimilarity extends AbstractNominalSimilarity {

	private static final long serialVersionUID = -4610984047997023498L;

	@Override
	protected double calculateSimilarity(double equalNonFalseValues, double unequalValues, double falseValues) {
		return equalNonFalseValues / (equalNonFalseValues + unequalValues + falseValues);
	}

	@Override
	public String toString() {
		return "Russell-Rao nominal similarity";
	}

}
