package io.sugo.pio.tools.math.similarity.numerical;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.similarity.DistanceMeasure;


/**
 * The euclidean distance.
 * 
 * @author Sebastian Land, Ingo Mierswa, Michael Wurst
 */
public class EuclideanDistance extends DistanceMeasure {

	private static final long serialVersionUID = -6802412021495569400L;

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
			return Math.sqrt(sum);
		} else {
			return Double.NaN;
		}
	}

	@Override
	public double calculateSimilarity(double[] value1, double[] value2) {
		return -calculateDistance(value1, value2);
	}

	// doing nothing
	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
		super.init(exampleSet);
		Tools.onlyNumericalAttributes(exampleSet, "value based similarities");
	}

	@Override
	public String toString() {
		return "Euclidean distance";
	}
}
