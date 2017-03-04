package io.sugo.pio.tools.math.similarity.divergences;

import io.sugo.pio.example.*;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.similarity.BregmanDivergence;


/**
 * The &quot;Generalized I-divergence &quot;.
 * 
 */
public class GeneralizedIDivergence extends BregmanDivergence {

	private static final long serialVersionUID = 5638471495692639837L;

	@Override
	public double calculateDistance(double[] value1, double[] value2) {
		double result = 0;
		double result2 = 0;
		for (int i = 0; i < value1.length; i++) {
			result += value1[i] * Math.log((value1[i]) / value2[i]);
			result2 += (value1[i] - value2[i]);
		}
		result = result - result2;
		return result;
	}

	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
		super.init(exampleSet);
		Tools.onlyNumericalAttributes(exampleSet, "value based similarities");
		Attributes attributes = exampleSet.getAttributes();
		for (Example example : exampleSet) {
			for (Attribute attribute : attributes) {
				if (example.getValue(attribute) <= 0) {
					throw new OperatorException(
							"The bregman divergence you've choosen is not applicable for the dataset! Proceeding with the 'Squared Euclidean distance' bregman divergence.");
				}
				;
			}
		}
	}

	@Override
	public String toString() {
		return "generalized I-divergence";
	}
}
