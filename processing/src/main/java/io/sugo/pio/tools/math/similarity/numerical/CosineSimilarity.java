package io.sugo.pio.tools.math.similarity.numerical;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.similarity.SimilarityMeasure;


/**
 * Cosine similarity that supports feature weights. If both vectors are empty or null vectors, NaN
 * is returned.
 * 
 */
public class CosineSimilarity extends SimilarityMeasure {

	private static final long serialVersionUID = 2856052490402674777L;

	@Override
	public double calculateSimilarity(double[] value1, double[] value2) {
		double sum = 0.0;
		double sum1 = 0.0;
		double sum2 = 0.0;
		for (int i = 0; i < value1.length; i++) {
			double v1 = value1[i];
			double v2 = value2[i];
			if ((!Double.isNaN(v1)) && (!Double.isNaN(v2))) {
				sum += v2 * v1;
				sum1 += v1 * v1;
				sum2 += v2 * v2;
			}
		}
		if ((sum1 > 0) && (sum2 > 0)) {
			double result = sum / (Math.sqrt(sum1) * Math.sqrt(sum2));
			// result can be > 1 (or -1) due to rounding errors for equal vectors, but must be
			// between -1 and 1
			return Math.min(Math.max(result, -1d), 1d);
			// return result;
		} else if (sum1 == 0 && sum2 == 0) {
			return 1d;
		} else {
			return 0d;
		}
	}

	@Override
	public double calculateDistance(double[] value1, double[] value2) {
		return Math.acos(calculateSimilarity(value1, value2));
	}

	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
		super.init(exampleSet);
		Tools.onlyNumericalAttributes(exampleSet, "value based similarities");
	}

	@Override
	public String toString() {
		return "Cosine similarity";
	}

}
