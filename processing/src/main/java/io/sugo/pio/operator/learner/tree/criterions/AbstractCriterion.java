package io.sugo.pio.operator.learner.tree.criterions;


import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.learner.tree.FrequencyCalculator;
import io.sugo.pio.operator.learner.tree.MinimalGainHandler;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.tools.Tools;

import static io.sugo.pio.operator.learner.tree.AbstractTreeLearner.CRITERIA_CLASSES;
import static io.sugo.pio.operator.learner.tree.AbstractTreeLearner.CRITERIA_NAMES;
import static io.sugo.pio.operator.learner.tree.AbstractTreeLearner.PARAMETER_CRITERION;

/**
 * This criterion class can be used for the incremental calculation of benefits.
 *
 * @author Sebastian Land
 */
public abstract class AbstractCriterion implements Criterion {

	// data for incremental calculation

	protected double leftWeight;
	protected double rightWeight;
	protected double totalWeight;
	protected double[] totalLabelWeights;
	protected double[] leftLabelWeights;
	protected double[] rightLabelWeights;
	protected Attribute labelAttribute;
	protected Attribute weightAttribute;

	@Override
	public boolean supportsIncrementalCalculation() {
		return false;
	}

	@Override
	public void startIncrementalCalculation(ExampleSet exampleSet) {
		FrequencyCalculator calculator = new FrequencyCalculator();
		rightLabelWeights = calculator.getLabelWeights(exampleSet);
		leftLabelWeights = new double[rightLabelWeights.length];
		totalLabelWeights = new double[rightLabelWeights.length];
		System.arraycopy(rightLabelWeights, 0, totalLabelWeights, 0, rightLabelWeights.length);
		leftWeight = 0;
		rightWeight = calculator.getTotalWeight(totalLabelWeights);
		totalWeight = rightWeight;

		labelAttribute = exampleSet.getAttributes().getLabel();
		weightAttribute = exampleSet.getAttributes().getWeight();
	}

	@Override
	public void swapExample(Example example) {
		double weight = 1;
		if (weightAttribute != null) {
			weight = example.getValue(weightAttribute);
		}
		int label = (int) example.getValue(labelAttribute);
		leftWeight += weight;
		rightWeight -= weight;
		leftLabelWeights[label] += weight;
		rightLabelWeights[label] -= weight;
	}

	@Override
	public double getIncrementalBenefit() {
		return 0;
	}

	/**
	 * This method returns the criterion specified by the respective parameters.
	 */
	public static Criterion createCriterion(ParameterHandler handler, double minimalGain) throws OperatorException {
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
				Criterion criterion = (Criterion) criterionClass.newInstance();
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
