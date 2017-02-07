package io.sugo.pio.operator.learner.tree;


import io.sugo.pio.operator.learner.tree.criterions.ColumnCriterion;
import io.sugo.pio.operator.learner.tree.criterions.WeightDistribution;
import io.sugo.pio.tools.Tools;

/**
 * Calculates the best split point for numerical attributes according to a given criterion.
 *
 * @author Ingo Mierswa, Gisa Schaefer
 */
public class ColumnNumericalSplitter {

	private ColumnCriterion criterion;
	private ColumnExampleTable columnTable;

	public ColumnNumericalSplitter(ColumnExampleTable columnTable, ColumnCriterion criterion) {
		this.criterion = criterion;
		this.columnTable = columnTable;
	}

	/**
	 * Calculates where to best split a numerical attribute by considering all possibilities and the
	 * associated benefits according to the given criterion. If there are missing values, they are
	 * considered as extra class.
	 *
	 * @param selectedExamples
	 *            which of the starting examples are considered sorted such the associated attribute
	 *            values are in ascending order
	 * @param attributeNumber
	 *            indicates which attribute is considered
	 * @return the benefit of the best split
	 */
	public ParallelBenefit getBestSplitBenefit(int[] selectedExamples, int attributeNumber) {
		final double[] attributeColumn = columnTable.getNumericalAttributeColumn(attributeNumber);

		double bestSplit = Double.NaN;
		double lastValue = Double.NaN;
		double bestSplitBenefit = Double.NEGATIVE_INFINITY;

		int lastRow = -1;
		WeightDistribution distribution = null;
		if (this.criterion.supportsIncrementalCalculation()) {
			distribution = this.criterion.startIncrementalCalculation(columnTable, selectedExamples, attributeNumber);
		}

		for (int j : selectedExamples) {

			double currentValue = attributeColumn[j];

			if (this.criterion.supportsIncrementalCalculation()) {
				if (lastRow > -1) {
					this.criterion.updateWeightDistribution(columnTable, lastRow, distribution);
				}
				lastRow = j;
				if (!Tools.isEqual(currentValue, lastValue)) {
					double benefit = this.criterion.getIncrementalBenefit(distribution);

					if (benefit > bestSplitBenefit) {
						bestSplitBenefit = benefit;
						bestSplit = (lastValue + currentValue) / 2.0d;
					}
				}

			} else {
				if (!Tools.isEqual(currentValue, lastValue)) {
					double splitValue = (lastValue + currentValue) / 2.0d;
					double benefit = this.criterion.getNumericalBenefit(columnTable, selectedExamples, attributeNumber,
							splitValue);
					if (benefit > bestSplitBenefit) {
						bestSplitBenefit = benefit;
						bestSplit = splitValue;
					}

				}
			}

			lastValue = currentValue;
		}

		if (Double.isNaN(bestSplit)) {
			return null;
		} else {
			return new ParallelBenefit(bestSplitBenefit, attributeNumber, bestSplit);
		}

	}

}
