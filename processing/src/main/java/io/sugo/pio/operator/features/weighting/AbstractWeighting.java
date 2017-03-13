package io.sugo.pio.operator.features.weighting;

import io.sugo.pio.example.AttributeWeights;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.error.ProcessSetupError.Severity;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.learner.CapabilityCheck;
import io.sugo.pio.operator.learner.CapabilityProvider;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeBoolean;
import io.sugo.pio.parameter.ParameterTypeCategory;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.metadata.*;
import io.sugo.pio.tools.ParameterService;
import io.sugo.pio.tools.Tools;

import java.util.List;

/**
 * This is an abstract superclass for RapidMiner weighting operators. New weighting schemes should
 * extend this class to support the same normalization parameter as other weighting operators.
 *
 * @author Helge Homburg
 */
public abstract class AbstractWeighting extends Operator implements CapabilityProvider {

	private InputPort exampleSetInput = getInputPorts().createPort("example set");
	private OutputPort weightsOutput = getOutputPorts().createPort("weights");
	private OutputPort exampleSetOutput = getOutputPorts().createPort("example set");
	private boolean checkForLabel;

	private static final String[] SORT_DIRECTIONS = new String[] { "ascending", "descending" };

	public static final int SORT_ASCENDING = 0;
	public static final int SORT_DESCENDING = 1;

	/** The parameter name for &quot;Activates the normalization of all weights.&quot; */
	public static final String PARAMETER_NORMALIZE_WEIGHTS = "normalize_weights";
	public static final String PARAMETER_SORT_WEIGHTS = "sort_weights";
	public static final String PARAMETER_SORT_DIRECTION = "sort_direction";

	/**
	 */
	public AbstractWeighting() {
		this(false);
	}

	/**
	 *
	 * @param name
	 *            name of the Operator
	 * @param checkForLabel
	 *            if no label exist, the operator throws an UserError and shows a MetaData warning
	 */
	public AbstractWeighting(boolean checkForLabel) {
		if (isExampleSetMandatory()) {
			exampleSetInput.addPrecondition(new CapabilityPrecondition(this, exampleSetInput));
		}
		this.checkForLabel = checkForLabel;

		getTransformer().addRule(new GenerateNewMDRule(weightsOutput, AttributeWeights.class));
		getTransformer().addRule(new ExampleSetPassThroughRule(exampleSetInput, exampleSetOutput, SetRelation.EQUAL) {

			@Override
			public ExampleSetMetaData modifyExampleSet(ExampleSetMetaData metaData) throws UndefinedParameterError {
				boolean normalizedWeights = getParameterAsBoolean(PARAMETER_NORMALIZE_WEIGHTS);
				for (AttributeMetaData amd : metaData.getAllAttributes()) {
					if (!amd.isSpecial() && amd.isNumerical()) {
						if (normalizedWeights) {
							amd.setValueSetRelation(SetRelation.SUBSET);
						} else {
							amd.setValueSetRelation(SetRelation.UNKNOWN);
						}
					}
				}
				if (AbstractWeighting.this.checkForLabel
						&& metaData.containsSpecialAttribute(Attributes.LABEL_NAME) != MetaDataInfo.YES) {
					addError(new SimpleMetaDataError(Severity.WARNING, exampleSetInput, "pio.error.metadata.exampleset.missing_role", "label"));
				}
				return super.modifyExampleSet(metaData);
			}
		});
	}

	protected abstract AttributeWeights calculateWeights(ExampleSet exampleSet) throws OperatorException;

	/**
	 * Helper method for anonymous instances of this class.
	 */
	public AttributeWeights doWork(ExampleSet exampleSet) throws OperatorException {
		exampleSetInput.receive(exampleSet);

		// check capabilities and produce errors if they are not fulfilled
		CapabilityCheck check = new CapabilityCheck(this, Tools.booleanValue(
				ParameterService.getParameterValue(PROPERTY_RAPIDMINER_GENERAL_CAPABILITIES_WARN), true)
				|| onlyWarnForNonSufficientCapabilities());
		check.checkLearnerCapabilities(this, exampleSet);

		doWork();
		return weightsOutput.getData(AttributeWeights.class);
	}

	@Override
	public void doWork() throws OperatorException {
		ExampleSet exampleSet = isExampleSetMandatory() ? exampleSetInput.getData(ExampleSet.class) : exampleSetInput
				.<ExampleSet> getDataOrNull(ExampleSet.class);
		if (checkForLabel && exampleSet.getAttributes().getLabel() == null) {
			throw new UserError(this, "pio.error.operator.exampleset_miss_label");
		}
		AttributeWeights weights = calculateWeights(exampleSet);
		if (getParameterAsBoolean(PARAMETER_NORMALIZE_WEIGHTS)) {
			weights.normalize();
		}
		if (getParameterAsBoolean(PARAMETER_SORT_WEIGHTS)) {
			weights.sort(getParameterAsInt(PARAMETER_SORT_DIRECTION) == SORT_ASCENDING ? AttributeWeights.INCREASING
					: AttributeWeights.DECREASING, AttributeWeights.ORIGINAL_WEIGHTS);
		}
		exampleSetOutput.deliver(exampleSet);
		weightsOutput.deliver(weights);
	}

	public InputPort getExampleSetInputPort() {
		return exampleSetInput;
	}

	public OutputPort getWeightsOutputPort() {
		return weightsOutput;
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> list = super.getParameterTypes();
		list.add(new ParameterTypeBoolean(PARAMETER_NORMALIZE_WEIGHTS, "Activates the normalization of all weights.", false));
		list.add(new ParameterTypeBoolean(PARAMETER_SORT_WEIGHTS, "If activated the weights will be returned sorted.", true));
		ParameterType type = new ParameterTypeCategory(PARAMETER_SORT_DIRECTION, "Defines the sorting direction.",
				SORT_DIRECTIONS, 0);
		type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_SORT_WEIGHTS, true, true));
		list.add(type);
		return list;
	}

	protected boolean isExampleSetMandatory() {
		return true;
	}

	protected boolean onlyWarnForNonSufficientCapabilities() {
		return false;
	}
}
