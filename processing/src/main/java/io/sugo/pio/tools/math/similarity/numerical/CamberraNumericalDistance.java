package io.sugo.pio.tools.math.similarity.numerical;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.similarity.DistanceMeasure;


/**
 * The Camberra distance measure.
 * 
 */
public class CamberraNumericalDistance extends DistanceMeasure {

	private static final long serialVersionUID = 1787035984412649082L;

	@Override
	public double calculateDistance(double[] value1, double[] value2) {
		double sum = 0.0;
		boolean changed = false;
		for (int i = 0; i < value1.length; i++) {
			double v1 = value1[i];
			double v2 = value2[i];
			if ((!Double.isNaN(v1)) && (!Double.isNaN(v2))) {
				sum = sum + Math.abs(v1 - v2) / Math.abs(v1 + v2);
				changed = true;
			}
		}
		return changed ? sum : Double.NaN;
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
		return "Camberra numerical distance";
	}

}
