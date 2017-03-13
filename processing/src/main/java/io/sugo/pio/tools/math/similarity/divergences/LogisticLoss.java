package io.sugo.pio.tools.math.similarity.divergences;

import io.sugo.pio.example.*;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.similarity.BregmanDivergence;


/**
 * The &quot;Logistic loss &quot;.
 * 
 */
public class LogisticLoss extends BregmanDivergence {

	private static final long serialVersionUID = 6209100890792566974L;

	@Override
	public double calculateDistance(double[] value1, double[] value2) {
		return value1[0] * Math.log(value1[0] / value2[0]) + (1 - value1[0]) * Math.log((1 - value1[0]) / (1 - value2[0]));
	}

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
				double value = example.getValue(attribute);
				if (value <= 0 || value >= 1) {
					throw new OperatorException(
							"The bregman divergence you've choosen is not applicable for the dataset! Proceeding with the 'Squared Euclidean distance' bregman divergence.");
				}
				;
			}
		}
	}

	@Override
	public String toString() {
		return "Logistic loss";
	}
}
