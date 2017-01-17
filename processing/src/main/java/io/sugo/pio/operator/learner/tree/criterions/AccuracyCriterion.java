package io.sugo.pio.operator.learner.tree.criterions;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.learner.tree.FrequencyCalculator;

/**
 * Calculates the accuracies for the given split if the children predict the majority classes.
 *
 * @author Ingo Mierswa
 */
public class AccuracyCriterion extends AbstractCriterion {

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
		double sum = 0.0d;
		for (int v = 0; v < weightCounts.length; v++) {
			int maxIndex = -1;
			double maxValue = Double.NEGATIVE_INFINITY;
			double currentSum = 0.0d;
			for (int l = 0; l < weightCounts[v].length; l++) {
				if (weightCounts[v][l] > maxValue) {
					maxIndex = l;
					maxValue = weightCounts[v][l];
				}
				currentSum += weightCounts[v][l];
			}
			sum += weightCounts[v][maxIndex] / currentSum;
		}
		return sum;
	}

	@Override
	public boolean supportsIncrementalCalculation() {
		return true;
	}

	@Override
	public double getIncrementalBenefit() {
		int maxIndex = -1;
		double maxValue = Double.NEGATIVE_INFINITY;
		double currentSum = 0.0d;
		for (int j = 0; j < leftLabelWeights.length; j++) {
			if (leftLabelWeights[j] > maxValue) {
				maxIndex = j;
				maxValue = leftLabelWeights[j];
			}
			currentSum += leftLabelWeights[j];
		}
		double sum = leftLabelWeights[maxIndex] / currentSum;
		maxIndex = -1;
		maxValue = Double.NEGATIVE_INFINITY;
		currentSum = 0.0d;
		for (int j = 0; j < rightLabelWeights.length; j++) {
			if (rightLabelWeights[j] > maxValue) {
				maxIndex = j;
				maxValue = rightLabelWeights[j];
			}
			currentSum += rightLabelWeights[j];
		}
		return sum + rightLabelWeights[maxIndex] / currentSum;
	}
}
