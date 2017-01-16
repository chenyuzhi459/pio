package io.sugo.pio.operator.learner.tree.criterions;


import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.learner.tree.FrequencyCalculator;

/**
 * The gain ratio divides the information gain by the prior split info in order to prevent id-like
 * attributes to be selected as the best.
 *
 * @author Sebastian Land, Ingo Mierswa
 */
public class GainRatioCriterion extends InfoGainCriterion {

	private static double LOG_FACTOR = 1d / Math.log(2);

	private FrequencyCalculator frequencyCalculator = new FrequencyCalculator();

	public GainRatioCriterion() {}

	public GainRatioCriterion(double minimalGain) {
		super(minimalGain);
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
		double gain = super.getBenefit(weightCounts);

		double splitInfo = getSplitInfo(weightCounts);

		if (splitInfo == 0) {
			return gain;
		} else {
			return gain / splitInfo;
		}
	}

	protected double getSplitInfo(double[][] weightCounts) {
		double[] splitCounts = new double[weightCounts.length];
		for (int v = 0; v < weightCounts.length; v++) {
			for (int l = 0; l < weightCounts[v].length; l++) {
				splitCounts[v] += weightCounts[v][l];
			}
		}

		double totalSplitCount = 0.0d;
		for (double w : splitCounts) {
			totalSplitCount += w;
		}

		double splitInfo = 0.0d;
		for (int v = 0; v < splitCounts.length; v++) {
			if (splitCounts[v] > 0) {
				double proportion = splitCounts[v] / totalSplitCount;
				splitInfo -= Math.log(proportion) * LOG_FACTOR * proportion;
			}
		}
		return splitInfo;
	}

	protected double getSplitInfo(double[] partitionWeights, double totalWeight) {
		double splitInfo = 0;
		for (double partitionWeight : partitionWeights) {
			if (partitionWeight > 0) {
				double partitionProportion = partitionWeight / totalWeight;
				splitInfo += partitionProportion * Math.log(partitionProportion) * LOG_FACTOR;
			}
		}
		return -splitInfo;
	}

	@Override
	public boolean supportsIncrementalCalculation() {
		return true;
	}

	@Override
	public double getIncrementalBenefit() {
		double gain = getEntropy(totalLabelWeights, totalWeight);
		gain -= getEntropy(leftLabelWeights, leftWeight) * leftWeight / totalWeight;
		gain -= getEntropy(rightLabelWeights, rightWeight) * rightWeight / totalWeight;
		double splitInfo = getSplitInfo(new double[] { leftWeight, rightWeight }, totalWeight);
		if (splitInfo == 0) {
			return gain;
		} else {
			return gain / splitInfo;
		}
	}
}
