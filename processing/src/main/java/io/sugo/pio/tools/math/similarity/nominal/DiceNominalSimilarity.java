package io.sugo.pio.tools.math.similarity.nominal;

/**
 * Implements the Dice similarity for nominal attributes.
 * 
 */
public class DiceNominalSimilarity extends AbstractNominalSimilarity {

	private static final long serialVersionUID = 2578286965200026417L;

	@Override
	protected double calculateSimilarity(double equalNonFalseValues, double unequalValues, double falseValues) {
		return 2 * equalNonFalseValues / (2 * equalNonFalseValues + unequalValues);
	}

	@Override
	public String toString() {
		return "Dice nominal similarity";
	}
}
