/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 *
 * Complete list of developers available at our web site:
 *
 * http://rapidminer.com
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.AttributeWeights;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.OperatorCapability;
import io.sugo.pio.operator.OperatorCreationException;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.features.weighting.ChiSquaredWeighting;
import io.sugo.pio.operator.learner.tree.criterions.Criterion;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.util.OperatorService;

import java.util.Iterator;
import java.util.List;

/**
 * The CHAID decision tree learner works like the
 * {@link io.sugo.pio.operator.learner.tree.DecisionTreeLearner} with one exception: it used a
 * chi squared based criterion instead of the information gain or gain ratio criteria.
 * 
 */
public class CHAIDLearner extends DecisionTreeLearner {

	@Override
	public String getName() {
		return "CHAIDLearner";
	}

	@Override
	public String getDefaultFullName() {
		return "CHAIDLearner";
	}

	@Override
	public String getDescription() {
		return "CHAIDLearner";
	}

	@Override
	public OperatorGroup getGroup() {
		return OperatorGroup.classification;
	}

	@Override
	protected Criterion createCriterion(double minimalGain) throws OperatorException {
		return new Criterion() {

			@Override
			public double getIncrementalBenefit() {
				throw new UnsupportedOperationException("Incremental calculation not supported.");
			}

			@Override
			public double getNominalBenefit(ExampleSet exampleSet, Attribute attribute) throws OperatorException {
				exampleSet = (ExampleSet) exampleSet.clone();
				exampleSet.getAttributes().clearRegular();
				exampleSet.getAttributes().addRegular(attribute);
				ChiSquaredWeighting weightOp = null;
				try {
					weightOp = OperatorService.createOperator(ChiSquaredWeighting.class);
				} catch (OperatorCreationException e) {
					throw new OperatorException("Cannot create chi squared calculation operator.", e);
				}
				AttributeWeights weights = weightOp.doWork(exampleSet);
				return weights.getWeight(attribute.getName());
			}

			@Override
			public double getNumericalBenefit(ExampleSet exampleSet, Attribute attribute, double splitValue) {
				throw new UnsupportedOperationException("Numerical attributes not supported.");
			}

			@Override
			public void startIncrementalCalculation(ExampleSet exampleSet) {
				throw new UnsupportedOperationException("Incremental calculation not supported.");
			}

			@Override
			public boolean supportsIncrementalCalculation() {
				return false;
			}

			@Override
			public void swapExample(Example example) {
				throw new UnsupportedOperationException("Incremental calculation not supported.");
			}

			@Override
			public double getBenefit(double[][] weightCounts) {
				throw new UnsupportedOperationException("Method not supported.");
			}
		};
	}

	/**
	 * This method calculates the benefit of the given attribute. This implementation utilizes the
	 * defined {@link Criterion}. Subclasses might want to override this method in order to
	 * calculate the benefit in other ways.
	 */
	protected Benefit calculateBenefit(ExampleSet trainingSet, Attribute attribute) throws OperatorException {
		ChiSquaredWeighting weightOp = null;
		try {
			weightOp = OperatorService.createOperator(ChiSquaredWeighting.class);
		} catch (OperatorCreationException e) {
			return null;
		}

		double weight = Double.NaN;
		if (weightOp != null) {
			AttributeWeights weights = weightOp.doWork(trainingSet);
			weight = weights.getWeight(attribute.getName());
		}

		if (!Double.isNaN(weight)) {
			return new Benefit(weight, attribute);
		} else {
			return null;
		}
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		// remove criterion selection
		Iterator<ParameterType> i = types.iterator();
		while (i.hasNext()) {
			if (i.next().getKey().equals(PARAMETER_CRITERION)) {
				i.remove();
			}
		}
		return types;
	}

	@Override
	public boolean supportsCapability(OperatorCapability capability) {
		if (capability == OperatorCapability.NUMERICAL_ATTRIBUTES) {
			return false;
		}
		return super.supportsCapability(capability);
	}
}
