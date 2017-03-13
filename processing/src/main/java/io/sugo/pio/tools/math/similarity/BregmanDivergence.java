package io.sugo.pio.tools.math.similarity;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;

/**
 * Abstract class that represents a bregman divergence.
 * 
 */
public abstract class BregmanDivergence extends DistanceMeasure {

	private static final long serialVersionUID = 5886004923294334118L;

	public double[] vectorSubtraction(Example x, double[] y) {
		if (x.getAttributes().size() != y.length) {
			throw new RuntimeException("Cannot substract vectors: incompatible numbers of attributes ("
					+ x.getAttributes().size() + " != " + y.length + ")!");
		}
		double[] result = new double[x.getAttributes().size()];
		int i = 0;
		for (Attribute att : x.getAttributes()) {
			result[i] = x.getValue(att) - y[i];
			i++;
		}
		return result;
	}

	public double logXToBaseY(double number, double base) {
		return Math.log(number) / Math.log(base);
	}

	@Override
	public double calculateSimilarity(double[] value1, double[] value2) {
		return -calculateDistance(value1, value2);
	}
}
