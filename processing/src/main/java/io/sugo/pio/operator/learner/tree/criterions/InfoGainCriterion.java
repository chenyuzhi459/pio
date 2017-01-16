package io.sugo.pio.operator.learner.tree.criterions;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.learner.tree.FrequencyCalculator;
import io.sugo.pio.operator.learner.tree.MinimalGainHandler;

/**
 * This criterion implements the well known information gain in order to calculate the benefit of a
 * split. The information gain is defined as the change in entropy from a prior state to a state
 * that takes some information as given by the entropy.
 *
 * @author Sebastian Land, Ingo Mierswa
 */
public class InfoGainCriterion extends AbstractCriterion implements MinimalGainHandler {

	private static double LOG_FACTOR = 1d / Math.log(2);

	private FrequencyCalculator frequencyCalculator = new FrequencyCalculator();

	private double minimalGain = 0.1;

	public InfoGainCriterion() {}

	public InfoGainCriterion(double minimalGain) {
		this.minimalGain = minimalGain;
	}

	@Override
	public void setMinimalGain(double minimalGain) {
		this.minimalGain = minimalGain;
	}

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
		int numberOfValues = weightCounts.length;
		int numberOfLabels = weightCounts[0].length;

		// calculate entropies
		double[] entropies = new double[numberOfValues];
		double[] totalWeights = new double[numberOfValues];
		for (int v = 0; v < numberOfValues; v++) {
			for (int l = 0; l < numberOfLabels; l++) {
				totalWeights[v] += weightCounts[v][l];
			}

			for (int l = 0; l < numberOfLabels; l++) {
				if (weightCounts[v][l] > 0) {
					double proportion = weightCounts[v][l] / totalWeights[v];
					entropies[v] -= Math.log(proportion) * LOG_FACTOR * proportion;
				}
			}
		}

		// calculate information amount WITH this attribute
		double totalWeight = 0.0d;
		for (double w : totalWeights) {
			totalWeight += w;
		}

		double information = 0.0d;
		for (int v = 0; v < numberOfValues; v++) {
			information += totalWeights[v] / totalWeight * entropies[v];
		}

		// calculate information amount WITHOUT this attribute
		double[] classWeights = new double[numberOfLabels];
		for (int l = 0; l < numberOfLabels; l++) {
			for (int v = 0; v < numberOfValues; v++) {
				classWeights[l] += weightCounts[v][l];
			}
		}

		double totalClassWeight = 0.0d;
		for (double w : classWeights) {
			totalClassWeight += w;
		}

		double classEntropy = 0.0d;
		for (int l = 0; l < numberOfLabels; l++) {
			if (classWeights[l] > 0) {
				double proportion = classWeights[l] / totalClassWeight;
				classEntropy -= Math.log(proportion) * LOG_FACTOR * proportion;
			}
		}

		// calculate and return information gain
		double informationGain = classEntropy - information;
		if (informationGain < minimalGain * classEntropy) {
			informationGain = 0;
		}
		return informationGain;
	}

	protected double getEntropy(double[] labelWeights, double totalWeight) {
		double entropy = 0;
		for (int i = 0; i < labelWeights.length; i++) {
			if (labelWeights[i] > 0) {
				double proportion = labelWeights[i] / totalWeight;
				entropy -= Math.log(proportion) * LOG_FACTOR * proportion;
			}
		}
		return entropy;
	}

	@Override
	public boolean supportsIncrementalCalculation() {
		return true;
	}

	@Override
	public double getIncrementalBenefit() {
		double totalEntropy = getEntropy(totalLabelWeights, totalWeight);
		double gain = getEntropy(leftLabelWeights, leftWeight) * leftWeight / totalWeight;
		gain += getEntropy(rightLabelWeights, rightWeight) * rightWeight / totalWeight;
		return totalEntropy - gain;
	}
}
