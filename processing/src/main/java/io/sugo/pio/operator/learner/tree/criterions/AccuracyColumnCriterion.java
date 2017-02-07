package io.sugo.pio.operator.learner.tree.criterions;


import io.sugo.pio.operator.learner.tree.FrequencyCalculator;
import io.sugo.pio.operator.learner.tree.MinimalGainHandler;

/**
 * Calculates the accuracies for the given split if the children predict the majority classes.
 *
 * @author Ingo Mierswa, Gisa Schaefer
 */
public class AccuracyColumnCriterion extends AbstractColumnCriterion implements MinimalGainHandler {

	private double minimalGain = 0.1;

	private FrequencyCalculator frequencyCalculator = new FrequencyCalculator();

	public AccuracyColumnCriterion() {}

	public AccuracyColumnCriterion(double minimalGain) {
		this.minimalGain = minimalGain;
	}

	@Override
	public void setMinimalGain(double minimalGain) {
		this.minimalGain = minimalGain;
	}

	@Override
	public double getBenefit(double[][] weightCounts) {
		int numberOfValues = weightCounts.length;
		int numberOfLabels = weightCounts[0].length;

		double totalSum = 0.0d;
		double sumOfMaximums = 0.0d;
		int differentValues = 0;
		for (int v = 0; v < numberOfValues; v++) {
			double maxValue = Double.NEGATIVE_INFINITY;
			double currentSum = 0.0d;
			for (int l = 0; l < numberOfLabels; l++) {
				if (weightCounts[v][l] > maxValue) {
					maxValue = weightCounts[v][l];
				}
				currentSum += weightCounts[v][l];
			}
			if (currentSum > 0) {
				differentValues++;
			}
			totalSum += currentSum;
			sumOfMaximums += maxValue;
		}

		// if the attribute has only one value left, discourage a split
		if (differentValues < 2) {
			return Double.NEGATIVE_INFINITY;
		}
		double accuracy = sumOfMaximums / totalSum;

		// check if the minimalGain needs to be checked
		if (minimalGain <= 0) {
			return accuracy;
		}
		// calculate the error before and after the split to check the minimal gain
		double error = 1 - accuracy;
		double[] classWeights = new double[numberOfLabels];
		for (int l = 0; l < numberOfLabels; l++) {
			for (int v = 0; v < numberOfValues; v++) {
				classWeights[l] += weightCounts[v][l];
			}
		}
		double maxValue = getMaximum(classWeights);

		double errorBefore = 1 - maxValue / frequencyCalculator.getTotalWeight(classWeights);
		// check if improvement is big enough
		if (errorBefore - error < minimalGain * errorBefore) {
			accuracy = 0;
		}
		return accuracy;
	}

	@Override
	public boolean supportsIncrementalCalculation() {
		return true;
	}

	@Override
	public double getIncrementalBenefit(WeightDistribution distribution) {
		double sumOfMax = getMaximum(distribution.getLeftLabelWeigths()) + getMaximum(distribution.getRightLabelWeigths());
		double totalSum = distribution.getLeftWeigth() + distribution.getRightWeigth();
		if (distribution.hasMissingValues()) {
			sumOfMax += getMaximum(distribution.getMissingsLabelWeigths());
			totalSum += distribution.getMissingsWeigth();
		}
		double accuracy = sumOfMax / totalSum;
		double accuracyBefore = getMaximum(distribution.getTotalLabelWeigths()) / distribution.getTotalWeigth();
		if (accuracy - accuracyBefore < minimalGain * (1 - accuracyBefore)) {
			return 0;
		}
		return accuracy;
	}

	private double getMaximum(double[] array) {
		double maxValue = Double.NEGATIVE_INFINITY;
		for (double entry : array) {
			if (entry > maxValue) {
				maxValue = entry;
			}
		}
		return maxValue;
	}

}
