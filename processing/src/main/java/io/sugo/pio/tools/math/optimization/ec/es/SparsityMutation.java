package io.sugo.pio.tools.math.optimization.ec.es;


import io.sugo.pio.tools.RandomGenerator;

import java.util.LinkedList;
import java.util.List;

/**
 * Checks for each value if it should mutated. Sets a non-min value to min and a min value to a
 * random value between min and max.
 * 
 * @author Ingo Mierswa
 */
public class SparsityMutation implements Mutation {

	private double prob;

	private double[] min, max;

	private OptimizationValueType[] valueTypes;

	private RandomGenerator random;

	public SparsityMutation(double prob, double[] min, double[] max, OptimizationValueType[] valueTypes,
			RandomGenerator random) {
		this.prob = prob;
		this.min = min;
		this.max = max;
		this.valueTypes = valueTypes;
		this.random = random;
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
			boolean changed = false;
			for (int j = 0; j < values.length; j++) {
				if (random.nextDouble() < prob) {
					changed = true;
					if (values[j] > min[j]) {
						values[j] = min[j];
					} else {
						values[j] = random.nextDoubleInRange(min[j], max[j]);
					}
					if (valueTypes[j].equals(OptimizationValueType.VALUE_TYPE_INT)) {
						values[j] = (int) Math.round(values[j]);
					} else if (valueTypes[j].equals(OptimizationValueType.VALUE_TYPE_BOUNDS)) {
						if (values[j] >= (max[j] - min[j]) / 2.0d) {
							values[j] = min[j];
						} else {
							values[j] = max[j];
						}
					}
				}
			}
			if (changed) {
				clone.setValues(values);
				newIndividuals.add(clone);
			}
		}
		population.addAll(newIndividuals);
	}
}
