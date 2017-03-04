package io.sugo.pio.tools.math.similarity.numerical;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.similarity.SimilarityMeasure;


/**
 * Specialized similarity that takes the maximum product of two feature values. If this value is
 * zero, the similarity is undefined. This similarity measure is used mainly with features extracted
 * from cluster models.
 * 
 */
public class MaxProductSimilarity extends SimilarityMeasure {

	private static final long serialVersionUID = -7476444724888001751L;

	@Override
	public double calculateSimilarity(double[] value1, double[] value2) {
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < value1.length; i++) {
			if ((!Double.isNaN(value1[i])) && (!Double.isNaN(value2[i]))) {
				double v = value2[i] * value1[i];
				if (v > max) {
					max = v;
				}
			}
		}
		if (max > 0.0) {
			return max;
		} else {
			return Double.NaN;
		}
	}

	@Override
	public double calculateDistance(double[] value1, double[] value2) {
		return -calculateSimilarity(value1, value2);
	}

	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
		super.init(exampleSet);
		Tools.onlyNumericalAttributes(exampleSet, "value based similarities");
	}

	@Override
	public String toString() {
		return "Max product numerical similarity";
	}

}
