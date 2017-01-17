package io.sugo.pio.operator.learner.tree.criterions;


import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.learner.tree.FrequencyCalculator;

/**
 * Calculates the Gini index for the given split.
 *
 * @author Ingo Mierswa
 */
public class GiniIndexCriterion extends AbstractCriterion {

	private FrequencyCalculator frequencyCalculator = new FrequencyCalculator();

	@Override
	public double getNominalBenefit(ExampleSet exampleSet, Attribute attribute) {
		double[][] weightCounts = frequencyCalculator.getNominalWeightCounts(exampleSet, attribute);
		return getBenefit(weightCounts);
	}

	@Override
	public double getNumericalBenefit(ExampleSet exampleSet, Attribute attribute, double splitValue) {
		double[][] weightCounts = frequencyCalculator.getNumericalWeightCounts(exampleSet, attribute, splitValue);
		return getBenefit(weightCounts);
	}

	@Override
	public double getBenefit(double[][] weightCounts) {
		// calculate information amount WITHOUT this attribute
		double[] classWeights = new double[weightCounts[0].length];
		for (int l = 0; l < classWeights.length; l++) {
			for (int v = 0; v < weightCounts.length; v++) {
				classWeights[l] += weightCounts[v][l];
			}
		}

		double totalClassWeight = frequencyCalculator.getTotalWeight(classWeights);

		double totalEntropy = getGiniIndex(classWeights, totalClassWeight);

		double gain = 0;
		for (int v = 0; v < weightCounts.length; v++) {
			double[] partitionWeights = weightCounts[v];
			double partitionWeight = frequencyCalculator.getTotalWeight(partitionWeights);
			gain += getGiniIndex(partitionWeights, partitionWeight) * partitionWeight / totalClassWeight;
		}
		return totalEntropy - gain;
	}

	private double getGiniIndex(double[] labelWeights, double totalWeight) {
		double sum = 0.0d;
		for (int i = 0; i < labelWeights.length; i++) {
			double frequency = labelWeights[i] / totalWeight;
			sum += frequency * frequency;
		}
		return 1.0d - sum;
	}

	@Override
	public boolean supportsIncrementalCalculation() {
		return true;
	}

	@Override
	public double getIncrementalBenefit() {
		double totalGiniEntropy = getGiniIndex(totalLabelWeights, totalWeight);
		double gain = getGiniIndex(leftLabelWeights, leftWeight) * leftWeight / totalWeight;
		gain += getGiniIndex(rightLabelWeights, rightWeight) * rightWeight / totalWeight;
		return totalGiniEntropy - gain;
	}
}
