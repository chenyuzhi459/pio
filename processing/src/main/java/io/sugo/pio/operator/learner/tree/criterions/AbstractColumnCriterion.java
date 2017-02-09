package io.sugo.pio.operator.learner.tree.criterions;


import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.learner.tree.ColumnExampleTable;
import io.sugo.pio.operator.learner.tree.ColumnFrequencyCalculator;
import io.sugo.pio.operator.learner.tree.MinimalGainHandler;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.tools.Tools;

import static io.sugo.pio.operator.learner.tree.AbstractParallelTreeLearner.CRITERIA_CLASSES;
import static io.sugo.pio.operator.learner.tree.AbstractParallelTreeLearner.CRITERIA_NAMES;
import static io.sugo.pio.operator.learner.tree.AbstractParallelTreeLearner.PARAMETER_CRITERION;

/**
 * This criterion class can be used for the incremental calculation of benefits.
 *
 * @author Sebastian Land, Gisa Schaefer
 */
public abstract class AbstractColumnCriterion implements ColumnCriterion {

	@Override
	public boolean supportsIncrementalCalculation() {
		return false;
	}

	@Override
	public WeightDistribution startIncrementalCalculation(ColumnExampleTable columnTable, int[] selection,
			int numericalAttributeNumber) {
		return new WeightDistribution(columnTable, selection, numericalAttributeNumber);
	}

	@Override
	public void updateWeightDistribution(ColumnExampleTable columnTable, int row, WeightDistribution distribution) {
		double weight = 1;
		if (columnTable.getWeight() != null) {
			weight = columnTable.getWeightColumn()[row];
		}
		int label = columnTable.getLabelColumn()[row];
		distribution.increment(label, weight);
	}

	@Override
	public double getNominalBenefit(ColumnExampleTable columnTable, int[] selection, int attributeNumber) {
		double[][] weightCounts = ColumnFrequencyCalculator.getNominalWeightCounts(columnTable, selection, attributeNumber);
		return getBenefit(weightCounts);
	}

	@Override
	public double getNumericalBenefit(ColumnExampleTable columnTable, int[] selection, int attributeNumber, double splitValue) {
		double[][] weightCounts = ColumnFrequencyCalculator.getNumericalWeightCounts(columnTable, selection,
				attributeNumber, splitValue);
		return getBenefit(weightCounts);
	}

	/**
	 * This method returns the criterion specified by the respective parameters.
	 */
	public static ColumnCriterion createColumnCriterion(ParameterHandler handler, double minimalGain)
			throws OperatorException {
		String criterionName = handler.getParameterAsString(PARAMETER_CRITERION);
		Class<?> criterionClass = null;
		for (int i = 0; i < CRITERIA_NAMES.length; i++) {
			if (CRITERIA_NAMES[i].equals(criterionName)) {
				criterionClass = CRITERIA_CLASSES[i];
			}
		}

		if (criterionClass == null && criterionName != null) {
			try {
				criterionClass = Tools.classForName(criterionName);
			} catch (ClassNotFoundException e) {
				throw new OperatorException("Cannot find criterion '" + criterionName
						+ "' and cannot instantiate a class with this name.");
			}
		}

		if (criterionClass != null) {
			try {
				ColumnCriterion criterion = (ColumnCriterion) criterionClass.newInstance();
				if (criterion instanceof MinimalGainHandler) {
					((MinimalGainHandler) criterion).setMinimalGain(minimalGain);
				}
				return criterion;
			} catch (InstantiationException e) {
				throw new OperatorException("Cannot instantiate criterion class '" + criterionClass.getName() + "'.");
			} catch (IllegalAccessException e) {
				throw new OperatorException("Cannot access criterion class '" + criterionClass.getName() + "'.");
			}
		} else {
			throw new OperatorException("No relevance criterion defined.");
		}
	}

}
