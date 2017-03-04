package io.sugo.pio.tools.math.similarity.nominal;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.similarity.DistanceMeasure;


/**
 * A distance measure for nominal values accounting a value of one if two values are unequal.
 * 
 */
public class NominalDistance extends DistanceMeasure {

	private static final long serialVersionUID = -1239573851325335924L;

	private boolean[] useAttribute;

	@Override
	public double calculateDistance(double[] value1, double[] value2) {
		double sum = 0.0;
		int counter = 0;

		for (int i = 0; i < value1.length; i++) {
			if (useAttribute == null || useAttribute[i]) {
				if ((!Double.isNaN(value1[i])) && (!Double.isNaN(value2[i]))) {
					if (value1[i] != value2[i]) {
						sum = sum + 1.0;
					}
					counter++;
				}
			}
		}

		if (counter > 0) {
			return sum;
		} else {
			return Double.NaN;
		}
	}

	@Override
	public double calculateSimilarity(double[] value1, double[] value2) {
		return -calculateDistance(value1, value2);
	}

	// checking for example set and valid attributes
	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
		super.init(exampleSet);
		Tools.onlyNominalAttributes(exampleSet, "nominal similarities");
		this.useAttribute = new boolean[exampleSet.getAttributes().size()];
		int i = 0;
		for (Attribute attribute : exampleSet.getAttributes()) {
			if (attribute.isNominal()) {
				useAttribute[i] = true;
			}
			i++;
		}
	}

	@Override
	public String toString() {
		return "Nominal distance";
	}
}
