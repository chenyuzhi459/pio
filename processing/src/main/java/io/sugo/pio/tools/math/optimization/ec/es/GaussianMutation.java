package io.sugo.pio.tools.math.optimization.ec.es;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Changes the values by adding a gaussian distribution multiplied with the current variance. Clips
 * the value range to [min,max].
 * 
 * @author Ingo Mierswa
 */
public class GaussianMutation implements Mutation {

	private double[] sigma;

	private double[] min, max;

	private OptimizationValueType[] valueTypes;

	private Random random;

	public GaussianMutation(double[] sigma, double[] min, double[] max, OptimizationValueType[] valueTypes, Random random) {
		this.sigma = sigma;
		this.min = min;
		this.max = max;
		this.valueTypes = valueTypes;
		this.random = random;
	}

	public void setSigma(double[] sigma) {
		this.sigma = sigma;
	}

	public double[] getSigma() {
		return this.sigma;
	}

	@Override
	public void setValueType(int index, OptimizationValueType type) {
		this.valueTypes[index] = type;
	}

	@Override
	public void operate(Population population) {
		List<Individual> newIndividuals = new LinkedList<Individual>();
		for (int i = 0; i < population.getNumberOfIndividuals(); i++) {
			Individual clone = (Individual) population.get(i).clone();
			double[] values = clone.getValues();
			for (int j = 0; j < values.length; j++) {
				if (valueTypes[j].equals(OptimizationValueType.VALUE_TYPE_INT)) {
					values[j] += random.nextGaussian() * sigma[j];
					values[j] = (int) Math.round(values[j]);
				} else if (valueTypes[j].equals(OptimizationValueType.VALUE_TYPE_BOUNDS)) {
					if (random.nextDouble() < 1.0d / values.length) {
						if (values[j] >= (max[j] - min[j]) / 2.0d) {
							values[j] = min[j];
						} else {
							values[j] = max[j];
						}
					}
				} else {
					values[j] += random.nextGaussian() * sigma[j];
				}

				if (values[j] < min[j]) {
					values[j] = min[j];
				}
				if (values[j] > max[j]) {
					values[j] = max[j];
				}
			}

			clone.setValues(values);
			newIndividuals.add(clone);
		}
		population.addAll(newIndividuals);
	}
}
