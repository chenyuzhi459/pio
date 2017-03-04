package io.sugo.pio.tools.math.similarity.divergences;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.similarity.BregmanDivergence;


/**
 * The &quot;Kullback-Leibler divergence &quot;.
 * 
 * @author Regina Fritsch
 */
public class KLDivergence extends BregmanDivergence {

	private static final long serialVersionUID = -2151832592670074328L;

	@Override
	public double calculateDistance(double[] value1, double[] value2) {
		double result = 0;
		for (int i = 0; i < value1.length; i++) {
			result += value1[i] * logXToBaseY(value1[i] / value2[i], 2);
		}
		return result;
	}

	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
		super.init(exampleSet);
		Tools.onlyNumericalAttributes(exampleSet, "value based similarities");
	}

	@Override
	public String toString() {
		return "Kullback-Leibler divergence";
	}
}
