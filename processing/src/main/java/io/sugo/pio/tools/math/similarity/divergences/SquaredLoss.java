package io.sugo.pio.tools.math.similarity.divergences;

import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.similarity.BregmanDivergence;


/**
 * The &quot;Squared loss &quot;.
 * 
 */
public class SquaredLoss extends BregmanDivergence {

	private static final long serialVersionUID = -449074179734918299L;

	@Override
	public double calculateDistance(double[] value1, double[] value2) {
		double diff = value1[0] - value2[0];
		return diff * diff;
	}

	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
		super.init(exampleSet);
		Tools.onlyNumericalAttributes(exampleSet, "value based similarities");
		Attributes attributes = exampleSet.getAttributes();
		if (attributes.size() != 1) {
			throw new OperatorException(
					"The bregman divergence you've choosen is not applicable for the dataset! Try 'Squared Euclidean distance' bregman divergence.");
		}
	}

	@Override
	public String toString() {
		return "Squared loss";
	}
}
