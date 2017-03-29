package io.sugo.pio.operator.preprocessing.normalization;

import io.sugo.pio.constant.PortConstant;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeCategory;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * This operator will transform a given Normalization Model into a model that will effectively
 * revert the normalization.
 * 
 * @author Sebastian Land
 */
public class DenormalizationOperator extends Operator {

	/**
	 * This saves the coefficients of a linear transformation a*x + b of attributes.
	 * 
	 * @author Sebastian Land
	 */
	public static class LinearTransformation implements Serializable {

		private static final long serialVersionUID = 1L;

		protected double a;
		protected double b;

		public LinearTransformation(double a, double b) {
			this.a = a;
			this.b = b;
		}
	}

	public static final String PARAMETER_MISSING_ATTRIBUTES_KEY = "missing_attribute_handling";
	public static final String PROCEED_ON_MISSING = "proceed on missing";
	public static final String FAIL_ON_MISSING = "fail_on_missing";
	public static final String[] PARAMETER_MISSING_ATTRIBUTES_OPTIONS = { PROCEED_ON_MISSING, FAIL_ON_MISSING };
	public static final int PARAMETER_MISSING_ATTRIBUTE_DEFAULT = 0;

	private boolean failOnMissingAttributes;

	private InputPort modelInput = getInputPorts().createPort(PortConstant.MODEL_INPUT, PortConstant.MODEL_INPUT_DESC, AbstractNormalizationModel.class);
	private OutputPort modelOutput = getOutputPorts().createPort(PortConstant.MODEL_OUTPUT, PortConstant.MODEL_OUTPUT_DESC);
	private OutputPort originalModelOutput = getOutputPorts().createPort(PortConstant.ORIGINAL_MODEL_OUTPUT, PortConstant.ORIGINAL_MODEL_OUTPUT_DESC);

	public DenormalizationOperator() {
		super();

		getTransformer().addPassThroughRule(modelInput, originalModelOutput);
		getTransformer().addGenerationRule(modelOutput, AbstractNormalizationModel.class);
	}

	@Override
	public String getDefaultFullName() {
		return null;
	}

	@Override
	public OperatorGroup getGroup() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}

	@Override
	public void doWork() throws OperatorException {
		AbstractNormalizationModel model = modelInput.getData(AbstractNormalizationModel.class);

		// check how to behave if an Attribute is missing in the input ExampleSet
		if (getParameter(PARAMETER_MISSING_ATTRIBUTES_KEY).equals(FAIL_ON_MISSING)) {
			failOnMissingAttributes = true;
		} else {
			failOnMissingAttributes = false;
		}

		Map<String, LinearTransformation> attributeTransformations = new HashMap<>();
		for (Attribute attribute : model.getTrainingHeader().getAttributes()) {
			double b = model.computeValue(attribute, 0);
			double a = model.computeValue(attribute, 1) - b;

			attributeTransformations.put(attribute.getName(), new LinearTransformation(a, b));
		}

		modelOutput.deliver(new DenormalizationModel(model.getTrainingHeader(), attributeTransformations, model,
				failOnMissingAttributes));
		originalModelOutput.deliver(model);
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();

		types.add(new ParameterTypeCategory(
				PARAMETER_MISSING_ATTRIBUTES_KEY,
				"Defines how the operator will act if attributes given to the Normalize operator are not present in the given model.",
				PARAMETER_MISSING_ATTRIBUTES_OPTIONS, PARAMETER_MISSING_ATTRIBUTE_DEFAULT));

		return types;
	}
}
