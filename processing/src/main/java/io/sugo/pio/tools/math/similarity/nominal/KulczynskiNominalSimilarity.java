package io.sugo.pio.tools.math.similarity.nominal;

/**
 * Implements the Kulczynski similarity for nominal attributes.
 * 
 */
public class KulczynskiNominalSimilarity extends AbstractNominalSimilarity {

	private static final long serialVersionUID = -3272271856059376791L;

	@Override
	protected double calculateSimilarity(double equalNonFalseValues, double unequalValues, double falseValues) {
		return equalNonFalseValues / unequalValues;
	}

	@Override
	public String toString() {
		return "Kulczynski nominal similarity";
	}

}
