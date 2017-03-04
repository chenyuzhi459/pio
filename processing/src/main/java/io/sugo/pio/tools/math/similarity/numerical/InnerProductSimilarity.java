package io.sugo.pio.tools.math.similarity.numerical;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.similarity.SimilarityMeasure;


/**
 * Similarity based on the inner product.
 * 
 */
public class InnerProductSimilarity extends SimilarityMeasure {

	private static final long serialVersionUID = -2564011779440607379L;

	@Override
	public double calculateSimilarity(double[] value1, double[] value2) {
		double sum = 0.0;
		for (int i = 0; i < value1.length; i++) {
			double v1 = value1[i];
			double v2 = value2[i];
			if ((!Double.isNaN(v1)) && (!Double.isNaN(v2))) {
				sum = sum + v2 * v1;
			}
		}
		return sum;
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
		return "Inner product similarity";
	}
}
