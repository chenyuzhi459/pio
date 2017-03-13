package io.sugo.pio.tools.math.similarity.mixed;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.tools.math.similarity.DistanceMeasure;


/**
 * Euclidean distance for numerical and nominal values. For nomimal values, a distance of one is
 * accounted if both values are not the same. Note: In most cases, you must normalize the numerical
 * values, to obtain sound results.
 * 
 */
public class MixedEuclideanDistance extends DistanceMeasure {

	private static final long serialVersionUID = 536655587492508882L;

	private boolean[] isNominal;

	@Override
	public double calculateDistance(double[] value1, double[] value2) {
		double sum = 0.0;
		int counter = 0;
		for (int i = 0; i < value1.length; i++) {
			if ((!Double.isNaN(value1[i])) && (!Double.isNaN(value2[i]))) {
				if (isNominal[i]) {
					if (value1[i] != value2[i]) {
						sum = sum + 1.0;
					}
				} else {
					double diff = value1[i] - value2[i];
					sum += diff * diff;
				}
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

	@Override
	public void init(ExampleSet exampleSet) throws OperatorException {
		super.init(exampleSet);
		Attributes attributes = exampleSet.getAttributes();
		init(attributes);
	}

	private void init(Attributes attributes) {
		isNominal = new boolean[attributes.size()];
		int index = 0;
		for (Attribute attribute : attributes) {
			isNominal[index++] = attribute.isNominal();
		}
	}

	@Override
	public DistanceMeasureConfig init(Attributes firstSetAttributes, Attributes secondSetAttributes) {
		DistanceMeasureConfig config = super.init(firstSetAttributes, secondSetAttributes);
		init(firstSetAttributes);
		return config;
	}

	@Override
	public String toString() {
		return "Mixed euclidean distance";
	}
}
