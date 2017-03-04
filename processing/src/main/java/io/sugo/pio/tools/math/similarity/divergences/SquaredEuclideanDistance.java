package io.sugo.pio.tools.math.similarity.divergences;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.similarity.BregmanDivergence;


/**
 * The &quot;Squared Euclidean distance &quot;.
 * 
 */
public class SquaredEuclideanDistance extends BregmanDivergence {

	private static final long serialVersionUID = -1506627696517615682L;

	@Override
	public double calculateDistance(double[] value1, double[] value2) {
		double sum = 0.0;
		int counter = 0;
		for (int i = 0; i < value1.length; i++) {
			if ((!Double.isNaN(value1[i])) && (!Double.isNaN(value2[i]))) {
				double diff = value1[i] - value2[i];
				sum += diff * diff;
				counter++;
			}
		}
		if (counter > 0) {
			return sum;
		} else {
			return Double.NaN;
		}
	}

	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
		super.init(exampleSet);
		Tools.onlyNumericalAttributes(exampleSet, "value based similarities");
	}

	@Override
	public String toString() {
		return "Squared euclidean distance";
	}
}
