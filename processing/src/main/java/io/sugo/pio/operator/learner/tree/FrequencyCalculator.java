package io.sugo.pio.operator.learner.tree;


import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.SplittedExampleSet;
import io.sugo.pio.tools.Tools;

/**
 * Calculates frequencies and weights.
 * 
 * @author Sebastian Land, Ingo Mierswa
 */
public class FrequencyCalculator {

	public FrequencyCalculator() {}

	public double[][] getNumericalWeightCounts(ExampleSet exampleSet, Attribute attribute, double splitValue) {
		Attribute label = exampleSet.getAttributes().getLabel();
		int numberOfLabels = label.getMapping().size();

		Attribute weightAttribute = exampleSet.getAttributes().getWeight();

		double[][] weightCounts = new double[2][numberOfLabels];

		for (Example example : exampleSet) {
			int labelIndex = (int) example.getValue(label);
			double value = example.getValue(attribute);

			double weight = 1.0d;
			if (weightAttribute != null) {
				weight = example.getValue(weightAttribute);
			}

			if (Tools.isLessEqual(value, splitValue)) {
				weightCounts[0][labelIndex] += weight;
			} else {
				weightCounts[1][labelIndex] += weight;
			}
		}

		return weightCounts;
	}

	public double[][] getNominalWeightCounts(ExampleSet exampleSet, Attribute attribute) {
		Attribute label = exampleSet.getAttributes().getLabel();
		int numberOfLabels = label.getMapping().size();
		int numberOfValues = attribute.getMapping().size();

		Attribute weightAttribute = exampleSet.getAttributes().getWeight();

		double[][] weightCounts = new double[numberOfValues][numberOfLabels];

		for (Example example : exampleSet) {
			int labelIndex = (int) example.getValue(label);
			double value = example.getValue(attribute);
			if (!Double.isNaN(value)) {
				int valueIndex = (int) value;
				double weight = 1.0d;
				if (weightAttribute != null) {
					weight = example.getValue(weightAttribute);
				}
				weightCounts[valueIndex][labelIndex] += weight;
			}
		}

		return weightCounts;
	}

	/**
	 * Returns an array of the size of the partitions. Each entry contains the sum of all weights of
	 * the corresponding partition.
	 */
	public double[] getPartitionWeights(SplittedExampleSet splitted) {
		Attribute weightAttribute = splitted.getAttributes().getWeight();
		double[] weights = new double[splitted.getNumberOfSubsets()];
		for (int i = 0; i < splitted.getNumberOfSubsets(); i++) {
			splitted.selectSingleSubset(i);
			for (Example e : splitted) {
				double weight = 1.0d;
				if (weightAttribute != null) {
					weight = e.getValue(weightAttribute);
				}
				weights[i] += weight;
			}
		}
		return weights;
	}

	/**
	 * Returns an array of size of the number of different label values. Each entry corresponds to
	 * the weight sum of all examples with the current label.
	 */
	public double[] getLabelWeights(ExampleSet exampleSet) {
		Attribute label = exampleSet.getAttributes().getLabel();
		Attribute weightAttribute = exampleSet.getAttributes().getWeight();
		double[] weights = new double[label.getMapping().size()];
		for (Example e : exampleSet) {
			int labelIndex = (int) e.getValue(label);
			double weight = 1.0d;
			if (weightAttribute != null) {
				weight = e.getValue(weightAttribute);
			}
			weights[labelIndex] += weight;
		}
		return weights;
	}

	/** Returns the sum of the given weights. */
	public double getTotalWeight(double[] weights) {
		double sum = 0.0d;
		for (double w : weights) {
			sum += w;
		}
		return sum;
	}
}
