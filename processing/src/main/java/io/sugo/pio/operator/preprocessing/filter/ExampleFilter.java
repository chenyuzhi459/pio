package io.sugo.pio.operator.preprocessing.filter;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.Condition;
import io.sugo.pio.example.set.ConditionedExampleSet;
import io.sugo.pio.example.set.CustomFilter;
import io.sugo.pio.example.table.AttributeTypeException;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.preprocessing.AbstractDataProcessing;
import io.sugo.pio.operator.tools.ExpressionEvaluationException;
import io.sugo.pio.parameter.*;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.ports.metadata.PassThroughRule;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>
 * This operator takes an {@link ExampleSet} as input and returns a new {@link ExampleSet} including
 * only the {@link Example}s that fulfill a condition.
 * </p>
 * <p>
 * <p>
 * By specifying an implementation of {@link io.sugo.pio.example.set.Condition} and a parameter
 * string, arbitrary filters can be applied. Users can implement their own conditions by writing a
 * subclass of the above class and implementing a two argument constructor taking an
 * {@link ExampleSet} and a parameter string. This parameter string is specified by the parameter
 * <code>parameter_string</code>. Instead of using one of the predefined conditions users can define
 * their own implementation with the fully qualified class name.
 * </p>
 * <p>
 * <p>
 * For &quot;attribute_value_condition&quot; the parameter string must have the form
 * <code>attribute op value</code>, where attribute is a name of an attribute, value is a value the
 * attribute can take and op is one of the binary logical operators similar to the ones known from
 * Java, e.g. greater than or equals. Please note your can define a logical OR of several conditions
 * with || and a logical AND of two conditions with two ampers and - or simply by applying several
 * ExampleFilter operators in a row. Please note also that for nominal attributes you can define a
 * regular expression for value of the possible equal and not equal checks.
 * </p>
 * <p>
 * <p>
 * For &quot;unknown_attributes&quot; the parameter string must be empty. This filter removes all
 * examples containing attributes that have missing or illegal values. For &quot;unknown_label&quot;
 * the parameter string must also be empty. This filter removes all examples with an unknown label
 * value.
 * </p>
 */
public class ExampleFilter extends AbstractDataProcessing {

    /**
     * The parameter name for &quot;Implementation of the condition.&quot;
     */
    public static final String PARAMETER_CONDITION_CLASS = "condition_class";

    /**
     * The parameter name for &quot;Parameter string for the condition, e.g. 'attribute=value' for
     * the AttributeValueFilter.&quot;
     */
    public static final String PARAMETER_PARAMETER_STRING = "parameter_string";

    /**
     * The parameter name for &quotParameter string for the expression, e.g. 'attribute1 ==
     * attribute2'.&quot;
     */
    public static final String PARAMETER_PARAMETER_EXPRESSION = "parameter_expression";

    /**
     * The parameter name for &quot;Defines the list of filters to apply.&quot;
     */
    public static final String PARAMETER_FILTER = "filters";

    /**
     * The parameter name for &quot;Indicates if only examples should be accepted which would
     * normally filtered.&quot;
     */
    public static final String PARAMETER_INVERT_FILTER = "invert_filter";

    /**
     * The hidden parameter for &quot;The list of filters.&quot;
     */
    public static final String PARAMETER_FILTERS_LIST = "filters_list";

    /**
     * The key parameter for the hidden {@value #PARAMETER_FILTERS_LIST} parameter
     */
    public static final String PARAMETER_FILTERS_ENTRY_KEY = "filters_entry_key";

    /**
     * The key parameter for the hidden {@value #PARAMETER_FILTERS_LIST} parameter
     */
    public static final String PARAMETER_FILTERS_ENTRY_VALUE = "filters_entry_value";

    /**
     * The hidden parameter for &quot;Logic operator for filters.&quot;
     */
    public static final String PARAMETER_FILTERS_LOGIC_AND = "filters_logic_and";

    /**
     * The hidden parameter for &quot;Check meta data for comparators.&quot;
     */
    public static final String PARAMETER_FILTERS_CHECK_METADATA = "filters_check_metadata";

    private final OutputPort unmatchedOutput = getOutputPorts().createPort("unmatched example set");

    public ExampleFilter() {
        getTransformer().addRule(new PassThroughRule(getInputPort(), unmatchedOutput, false) {

            @Override
            public MetaData modifyMetaData(MetaData metaData) {
                if (metaData instanceof ExampleSetMetaData) {
                    return ExampleFilter.this.modifyMetaData((ExampleSetMetaData) metaData);
                } else {
                    return metaData;
                }
            }
        });
    }

    @Override
    public ExampleSetMetaData modifyMetaData(final ExampleSetMetaData emd) {
        emd.getNumberOfExamples().reduceByUnknownAmount();
//		try {
//			if (getParameterAsString(PARAMETER_CONDITION_CLASS).equals(
//					ConditionedExampleSet.KNOWN_CONDITION_NAMES[ConditionedExampleSet.CONDITION_NO_MISSING_ATTRIBUTES])) {
//				for (AttributeMetaData amd : emd.getAllAttributes()) {
//					amd.setNumberOfMissingValues(new MDInteger(0));
//				}
//			}
//		} catch (UndefinedParameterError e) {
//		}
        return emd;
    }

    @Override
    public ExampleSet apply(final ExampleSet inputSet) throws OperatorException {
        getLogger().fine(getName() + ": input set has " + inputSet.size() + " examples.");

//		String className = getParameterAsString(PARAMETER_CONDITION_CLASS);
//		String parameter = getParameterAsString(PARAMETER_PARAMETER_STRING);
//		getLogger().fine("Creating condition '" + className + "' with parameter '" + parameter + "'");
        String className = ConditionedExampleSet.KNOWN_CONDITION_NAMES[ConditionedExampleSet.CONDITION_CUSTOM_FILTER];
        Condition condition = null;
        try {
            String rawParameterString = getParameters().getParameterAsSpecified(PARAMETER_FILTERS_LIST);
            List<String[]> operatorFilterList = ParameterTypeList.transformString2List(rawParameterString);
            condition = new CustomFilter(inputSet, operatorFilterList,
                    getParameterAsBoolean(PARAMETER_FILTERS_LOGIC_AND), getProcess().getMacroHandler());

        } catch (AttributeTypeException e) {
            throw new UserError(this, e, "filter_wrong_type", e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new UserError(this, e, 904, className, e.getMessage());
        }
        try {
            ExampleSet result = new ConditionedExampleSet(inputSet, condition,
                    getParameterAsBoolean(PARAMETER_INVERT_FILTER), getProgress());
            if (unmatchedOutput.isConnected()) {
                ExampleSet unmatchedResult = new ConditionedExampleSet(inputSet, condition,
                        !getParameterAsBoolean(PARAMETER_INVERT_FILTER));
                unmatchedOutput.deliver(unmatchedResult);
            }
            return result;
        } catch (AttributeTypeException e) {
            throw new UserError(this, e, "filter_wrong_type", e.getMessage());
        } catch (ExpressionEvaluationException e) {
            throw new UserError(this, e, 904, className, e.getMessage());
        }
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.ExampleFilter.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.processing;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.ExampleFilter.description");
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        ParameterType type;

//        type = new ParameterTypeFilter(PARAMETER_FILTER, "Defines the list of filters to apply.",
//                getInputPort(), true);
//        type.registerDependencyCondition(new EqualStringCondition(this, PARAMETER_CONDITION_CLASS, false,
//                ConditionedExampleSet.KNOWN_CONDITION_NAMES[ConditionedExampleSet.CONDITION_CUSTOM_FILTER]));
//        types.add(type);

//		type = new ParameterTypeString(PARAMETER_PARAMETER_STRING,
//				"Parameter string for the condition, e.g. 'attribute=value' for the AttributeValueFilter.", true);
//		type.registerDependencyCondition(new EqualStringCondition(this, PARAMETER_CONDITION_CLASS, true,
//				ConditionedExampleSet.KNOWN_CONDITION_NAMES[ConditionedExampleSet.CONDITION_ATTRIBUTE_VALUE_FILTER]));
////		type.setExpert(false);
//		types.add(type);

//		type = new ParameterTypeExpression(PARAMETER_PARAMETER_EXPRESSION,
//				"Parameter string for the expression, e.g. 'attribute1 == attribute2'.", getInputPort(), true);
//		type.registerDependencyCondition(new EqualStringCondition(this, PARAMETER_CONDITION_CLASS, true,
//				ConditionedExampleSet.KNOWN_CONDITION_NAMES[ConditionedExampleSet.CONDITION_EXPRESSION]));
////		type.setExpert(false);
//		types.add(type);

//		type = new ParameterTypeStringCategory(PARAMETER_CONDITION_CLASS, "Implementation of the condition.",
//				ConditionedExampleSet.KNOWN_CONDITION_NAMES,
//				ConditionedExampleSet.KNOWN_CONDITION_NAMES[ConditionedExampleSet.CONDITION_CUSTOM_FILTER], false);
////		type.setExpert(true); // confusing, only show for experts, default custom filters are fine
//		// for new users
//		types.add(type);

//		type = new ParameterTypeBoolean(PARAMETER_INVERT_FILTER,
//				"Indicates if only examples should be accepted which would normally be filtered.", false);
////		type.setExpert(false);
//		types.add(type);

        // hidden parameter, only used to store the filters set via the ParameterTypeFilter dialog
        // above
//        type = new ParameterTypeList(PARAMETER_FILTERS_LIST, I18N.getMessage("pio.ExampleFilter.filters_list"), new ParameterTypeString(
//                PARAMETER_FILTERS_ENTRY_KEY, "A key entry of the filters list."), new ParameterTypeString(
//                PARAMETER_FILTERS_ENTRY_VALUE, "A value entry of the filters list."));
//        type.setHidden(true);
//		type.registerDependencyCondition(new EqualStringCondition(this, PARAMETER_CONDITION_CLASS, true,
//				ConditionedExampleSet.KNOWN_CONDITION_NAMES[8]));
//        types.add(type);

        List<CompareTuple> compareOpts = new ArrayList<>();
        CustomFilter.CustomFilters[] filters = CustomFilter.CustomFilters.values();
        for (CustomFilter.CustomFilters filter : filters) {
            compareOpts.add(new CompareTuple(filter.getSymbol(), filter.getValueType()));
        }
        type = new ParameterTypeCompare(PARAMETER_FILTERS_LIST, I18N.getMessage("pio.ExampleFilter.filters_list"), getInputPort(), compareOpts);
        types.add(type);

        // hidden parameter, only used to store if the filters from the ParameterTypeFilter dialog
        // above should be ANDed or ORed
        type = new ParameterTypeBoolean(PARAMETER_FILTERS_LOGIC_AND, I18N.getMessage("pio.ExampleFilter.filters_logic_and"), true);
        types.add(type);

        // hidden parameter, only used to store if the meta data should be checked in the
        // ParameterTypeFilter dialog
//		type = new ParameterTypeBoolean(PARAMETER_FILTERS_CHECK_METADATA, "Check meta data for comparators.", true);
//		type.setHidden(true);
//		type.registerDependencyCondition(new EqualStringCondition(this, PARAMETER_CONDITION_CLASS, true,
//				ConditionedExampleSet.KNOWN_CONDITION_NAMES[8]));
//		types.add(type);

        return types;
    }

    @Override
    public boolean writesIntoExistingData() {
        return false;
    }

//	@Override
//	public ResourceConsumptionEstimator getResourceConsumptionEstimator() {
//		return OperatorResourceConsumptionHandler.getResourceConsumptionEstimator(getInputPort(), ExampleFilter.class, null);
//	}
//
//	@Override
//	public OperatorVersion[] getIncompatibleVersionChanges() {
//		return ExpressionParserUtils.addIncompatibleExpressionParserChange(super.getIncompatibleVersionChanges());
//	}

}
