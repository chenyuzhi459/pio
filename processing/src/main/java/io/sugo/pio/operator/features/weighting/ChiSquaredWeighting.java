package io.sugo.pio.operator.features.weighting;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.AttributeWeights;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.*;
import io.sugo.pio.operator.preprocessing.discretization.BinDiscretization;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeInt;
import io.sugo.pio.tools.math.ContingencyTableTools;
import io.sugo.pio.util.OperatorService;

import java.util.List;

/**
 * This operator calculates the relevance of a feature by computing for each attribute of the input
 * example set the value of the chi-squared statistic with respect to the class attribute.
 *
 * @author Ingo Mierswa
 */
public class ChiSquaredWeighting extends AbstractWeighting {

	private static final int PROGRESS_UPDATE_STEPS = 1_000_000;

	public ChiSquaredWeighting() {
		super(true);
	}

	@Override
	public String getName() {
		return "chiSquaredWeighting";
	}

	@Override
	public String getDefaultFullName() {
		return "chiSquaredWeighting";
	}

	@Override
	public String getDescription() {
		return "chiSquaredWeighting";
	}

	@Override
	public OperatorGroup getGroup() {
		return OperatorGroup.algorithmModel;
	}

	@Override
	protected AttributeWeights calculateWeights(ExampleSet exampleSet) throws OperatorException {
		Attribute label = exampleSet.getAttributes().getLabel();
		if (!label.isNominal()) {
			throw new UserError(this, "pio.error.attribute_must_nominal",
					"chi squared test", label.getName());
		}

		BinDiscretization discretization = null;
		try {
			discretization = OperatorService.createOperator(BinDiscretization.class);
		} catch (OperatorCreationException e) {
			throw new UserError(this, "pio.error.cannot_instantiate",
					"Discretization", e.getMessage());
		}

		int numberOfBins = getParameterAsInt(BinDiscretization.PARAMETER_NUMBER_OF_BINS);
		discretization.setParameter(BinDiscretization.PARAMETER_NUMBER_OF_BINS, numberOfBins + "");
		exampleSet = discretization.doWork(exampleSet);

		int maximumNumberOfNominalValues = 0;
		for (Attribute attribute : exampleSet.getAttributes()) {
			if (attribute.isNominal()) {
				maximumNumberOfNominalValues = Math.max(maximumNumberOfNominalValues, attribute.getMapping().size());
			}
		}

		if (numberOfBins < maximumNumberOfNominalValues) {
			numberOfBins = maximumNumberOfNominalValues;
		}

		// init
		double[][][] counters = new double[exampleSet.getAttributes().size()][numberOfBins][label.getMapping().size()];
		Attribute weightAttribute = exampleSet.getAttributes().getWeight();

		// count
		double[] temporaryCounters = new double[label.getMapping().size()];
		for (Example example : exampleSet) {
			double weight = 1.0d;
			if (weightAttribute != null) {
				weight = example.getValue(weightAttribute);
			}
			int labelIndex = (int) example.getLabel();
			temporaryCounters[labelIndex] += weight;
		}

		for (int k = 0; k < counters.length; k++) {
			for (int i = 0; i < temporaryCounters.length; i++) {
				counters[k][0][i] = temporaryCounters[i];
			}
		}

		// attribute counts
		getProgress().setTotal(100);
		long progressCounter = 0;
		double totalProgress = exampleSet.size() * exampleSet.getAttributes().size();
		for (Example example : exampleSet) {
			int labelIndex = (int) example.getLabel();
			double weight = 1.0d;
			if (weightAttribute != null) {
				weight = example.getValue(weightAttribute);
			}
			int attributeCounter = 0;
			for (Attribute attribute : exampleSet.getAttributes()) {
				int attributeIndex = (int) example.getValue(attribute);
				counters[attributeCounter][attributeIndex][labelIndex] += weight;
				counters[attributeCounter][0][labelIndex] -= weight;
				attributeCounter++;
				if (++progressCounter % PROGRESS_UPDATE_STEPS == 0) {
					getProgress().setCompleted((int) (100 * (progressCounter / totalProgress)));
				}
			}
		}

		// calculate the actual chi-squared values and assign them to weights
		AttributeWeights weights = new AttributeWeights(exampleSet);
		int attributeCounter = 0;
		for (Attribute attribute : exampleSet.getAttributes()) {
			double weight = ContingencyTableTools
					.getChiSquaredStatistics(ContingencyTableTools.deleteEmpty(counters[attributeCounter]), false);
			weights.setWeight(attribute.getName(), weight);
			attributeCounter++;
		}

		return weights;
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		types.add(new ParameterTypeInt(BinDiscretization.PARAMETER_NUMBER_OF_BINS,
				"The number of bins used for discretization of numerical attributes before the chi squared test can be performed.",
				2, Integer.MAX_VALUE, 10));
		return types;
	}

	@Override
	public boolean supportsCapability(OperatorCapability capability) {
		switch (capability) {
			case BINOMINAL_ATTRIBUTES:
			case POLYNOMINAL_ATTRIBUTES:
			case NUMERICAL_ATTRIBUTES:
			case BINOMINAL_LABEL:
			case POLYNOMINAL_LABEL:
				return true;
			default:
				return false;
		}
	}
}
