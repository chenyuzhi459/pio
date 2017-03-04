package io.sugo.pio.tools.math.similarity.numerical;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.similarity.DistanceMeasure;


/**
 * This measure returns the maximal individual absolute distance of both examples in any component.
 * 
 */
public class ChebychevNumericalDistance extends DistanceMeasure {

	private static final long serialVersionUID = -2995153254013795660L;

	@Override
	public double calculateDistance(double[] value1, double[] value2) {
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < value1.length; i++) {
			double v1 = value1[i];
			double v2 = value2[i];
			if ((!Double.isNaN(v1)) && (!Double.isNaN(v2))) {
				double d = Math.abs(v1 - v2);
				if (d > max) {
					max = d;
				}
			}
		}
		if (max > Double.NEGATIVE_INFINITY) {
			return max;
		} else {
			return Double.NaN;
		}
	}

	@Override
	public double calculateSimilarity(double[] value1, double[] value2) {
		return -calculateDistance(value1, value2);
	}

	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
		super.init(exampleSet);
		Tools.onlyNumericalAttributes(exampleSet, "value based similarities");
	}

	@Override
	public String toString() {
		return "Chebychev numerical distance";
	}

}
