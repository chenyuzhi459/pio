package io.sugo.pio.tools.math.similarity.numerical;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.MathFunctions;
import io.sugo.pio.tools.math.similarity.SimilarityMeasure;


/**
 * Similarity based on the correlation coefficient.
 * 
 */
public class CorrelationSimilarity extends SimilarityMeasure {

	private static final long serialVersionUID = 7106870911590574668L;

	@Override
	public double calculateSimilarity(double[] value1, double[] value2) {
		return MathFunctions.correlation(value1, value2);
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
		return "Correlation based similarity";
	}
}
