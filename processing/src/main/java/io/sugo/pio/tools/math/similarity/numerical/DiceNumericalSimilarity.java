package io.sugo.pio.tools.math.similarity.numerical;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.similarity.SimilarityMeasure;


/**
 * A variant of the Dice coefficient defined for numeric attributes.
 * 
 */
public class DiceNumericalSimilarity extends SimilarityMeasure {

	private static final long serialVersionUID = 7556034451164266813L;

	@Override
	public double calculateSimilarity(double[] value1, double[] value2) {
		double wxy = 0.0;
		double wx = 0.0;
		double wy = 0.0;
		for (int i = 0; i < value1.length; i++) {
			if ((!Double.isNaN(value1[i])) && (!Double.isNaN(value2[i]))) {
				wx = wx + value1[i];
				wy = wy + value2[i];
				wxy = wxy + value1[i] * value2[i];
			}
		}
		return 2 * wxy / (wx + wy);
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
		return "Dice numerical similarity";
	}
}
