package io.sugo.pio.tools.math.similarity.divergences;

import io.sugo.pio.example.*;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.similarity.BregmanDivergence;


/**
 * The &quot;Logarithmic loss &quot;.
 * 
 */
public class LogarithmicLoss extends BregmanDivergence {

	private static final long serialVersionUID = 871453359959645339L;

	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
		super.init(exampleSet);
		Tools.onlyNumericalAttributes(exampleSet, "value based similarities");
		Attributes attributes = exampleSet.getAttributes();
		if (attributes.size() != 1) {
			throw new OperatorException(
					"The bregman divergence you've choosen is not applicable for the dataset! Proceeding with the 'Squared Euclidean distance' bregman divergence.");
		}
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
	public double calculateDistance(double[] value1, double[] value2) {
		return (value1[0] * Math.log(value1[0] / value2[0])) - (value1[0] - value2[0]);
	}

	@Override
	public String toString() {
		return "Logarithmic loss";
	}
}
