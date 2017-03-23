package io.sugo.pio.tools;

import io.sugo.pio.example.*;
import io.sugo.pio.example.set.ConditionCreationException;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.error.AttributeNotFoundError;
import io.sugo.pio.operator.error.ProcessSetupError.Severity;
import io.sugo.pio.operator.preprocessing.filter.attributes.*;
import io.sugo.pio.operator.preprocessing.filter.attributes.AttributeFilterCondition.ScanResult;
import io.sugo.pio.parameter.*;
import io.sugo.pio.parameter.conditions.EqualTypeCondition;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.metadata.*;

import java.util.*;

import static io.sugo.pio.parameter.ParameterTypeAttributes.ATTRIBUTE_SEPARATOR_CHARACTER;

/**
 * An AttributeSubsetSelector provides conditions for filtering attributes from example sets. It
 * also provides parameters for the selection of subsets which can be used in operators that should
 * work on a subset of the available attributes. According to the specified parameters, the
 * available methods select the subsets in both meta data and actual data.
 */
public class AttributeSubsetSelector {

    /**
     * The parameter name for &quot;Implementation of the condition.&quot;
     */
    public static final String PARAMETER_FILTER_TYPE = "attribute_filter_type";

    public static final int PARAMETER_DEFAULT_FILTER_TYPE = 2;

    /**
     * The parameter name for &quot;Indicates if only examples should be accepted which would
     * normally filtered.&quot;
     */
    public static final String PARAMETER_INVERT_SELECTION = "invert_selection";

    public static final String PARAMETER_INCLUDE_SPECIAL_ATTRIBUTES = "include_special_attributes";

    /*public static String[] CONDITION_NAMES = new String[]{"all", "single", "subset", "regular_expression", "value_type",
            "block_type", "no_missing_values", "numeric_value_filter"};*/
    public static String[] CONDITION_NAMES = new String[]{
            io.sugo.pio.i18n.I18N.getMessage("pio.AttributeSubsetSelector.condition_names.all"),
            io.sugo.pio.i18n.I18N.getMessage("pio.AttributeSubsetSelector.condition_names.single"),
            io.sugo.pio.i18n.I18N.getMessage("pio.AttributeSubsetSelector.condition_names.subset"),
            io.sugo.pio.i18n.I18N.getMessage("pio.AttributeSubsetSelector.condition_names.regular_expression"),
            io.sugo.pio.i18n.I18N.getMessage("pio.AttributeSubsetSelector.condition_names.value_type"),
            io.sugo.pio.i18n.I18N.getMessage("pio.AttributeSubsetSelector.condition_names.block_type"),
            io.sugo.pio.i18n.I18N.getMessage("pio.AttributeSubsetSelector.condition_names.no_missing_values"),
            io.sugo.pio.i18n.I18N.getMessage("pio.AttributeSubsetSelector.condition_names.numeric_value_filter")
    };

    private static Class<?>[] CONDITION_IMPLEMENTATIONS = {TransparentAttributeFilter.class, SingleAttributeFilter.class,
            SubsetAttributeFilter.class, RegexpAttributeFilter.class, ValueTypeAttributeFilter.class,
            BlockTypeAttributeFilter.class, NoMissingValuesAttributeFilter.class, NumericValueAttributeFilter.class};

    public static final int CONDITION_ALL = 0;

    public static final int CONDITION_SINGLE = 1;

    public static final int CONDITION_SUBSET = 2;

    public static final int CONDITION_REGULAR_EXPRESSION = 3;

    public static final int CONDITION_VALUE_TYPE = 5;

    public static final int CONDITION_BLOCK_TYPE = 6;

    public static final int CONDITION_NO_MISSING_VALUES = 7;

    public static final int CONDITION_NUMERIC_VALUE_FILTER = 8;

    private final ParameterHandler operator;

    private final InputPort inPort;

    private int[] valueTypes;

    public AttributeSubsetSelector(ParameterHandler operator, InputPort inPort) {
        this(operator, inPort, Ontology.ATTRIBUTE_VALUE);
    }

    public AttributeSubsetSelector(ParameterHandler operator, InputPort inPort, int... valueTypes) {
        this.operator = operator;
        this.inPort = inPort;
        if (valueTypes.length == 0) {
            this.valueTypes = new int[]{Ontology.ATTRIBUTE_VALUE};
        } else {
            this.valueTypes = valueTypes;
        }
    }

    /**
     * This method returns the meta data of an exampleset as if it would have been filtered. If
     * exceptions are thrown because of wrong or missing parameter settings, null will be returned.
     * Please keep in mind that the resulting ExampleSetMetaData are only a clone of the original
     * one. Changes must hence be performed on the orginial exampleSetMetaData object.
     */
    public ExampleSetMetaData getMetaDataSubset(ExampleSetMetaData metaData, boolean keepSpecialIfNotIncluded,
                                                boolean errorOnMissing) {
        try {
            Collection<AttributeMetaData> attributes = metaData.getAllAttributes();
//            AttributeFilterCondition condition = createCondition(
//                    CONDITION_NAMES[operator.getParameterAsInt(PARAMETER_FILTER_TYPE)], operator);
            AttributeFilterCondition condition = createCondition(
                    CONDITION_NAMES[PARAMETER_DEFAULT_FILTER_TYPE], operator);
            ExampleSetMetaData resultSet = metaData.clone();

            // init
            boolean invert = operator.getParameterAsBoolean(PARAMETER_INVERT_SELECTION);
            boolean applyOnSpecial = operator.getParameterAsBoolean(PARAMETER_INCLUDE_SPECIAL_ATTRIBUTES);
            boolean foundUnknown = false;

            for (AttributeMetaData attribute : attributes) {
                if (!attribute.isSpecial() || attribute.isSpecial() && applyOnSpecial) {
                    MetaDataInfo result = condition.isFilteredOutMetaData(attribute, operator);
                    if (invert) {
                        if (result == MetaDataInfo.NO) {
                            result = MetaDataInfo.YES;
                        } else if (result == MetaDataInfo.YES) {
                            result = MetaDataInfo.NO;
                        }
                    }
                    if (result == MetaDataInfo.YES) {
                        resultSet.removeAttribute(attribute);
                    }
                    if (result == MetaDataInfo.UNKNOWN) {
                        foundUnknown = true;
                    }
                } else if (!keepSpecialIfNotIncluded) {
                    resultSet.removeAttribute(attribute);
                }
            }
            if (applyOnSpecial) {
                for (AttributeMetaData attribute : resultSet.getAllAttributes()) {
                    attribute.setRegular();
                }
            }

            // now remove non-special attributes of wrong type
            Iterator<AttributeMetaData> iterator = resultSet.getAllAttributes().iterator();
            while (iterator.hasNext()) {
                AttributeMetaData attribute = iterator.next();
                if (!attribute.isSpecial()) {
                    boolean keep = isOfAllowedType(attribute.getValueType());
                    if (!keep) {
                        iterator.remove();
                    }
                }
            }

            // now check if unknown was found: Set set relation
            if (foundUnknown) {
                resultSet.attributesAreSubset();
            }

            switch (metaData.getAttributeSetRelation()) {
                case SUBSET:
                    resultSet.attributesAreSubset();
                    break;
                case SUPERSET:
                    resultSet.attributesAreSuperset();
                    break;
                case EQUAL:
                case UNKNOWN:
                    break;
            }

            if (errorOnMissing) {
                if (condition instanceof SingleAttributeFilter) {
                    if (resultSet.getAllAttributes().size() != 1) {
                        inPort.addError(new SimpleMetaDataError(Severity.ERROR, inPort,
                                "pio.error.metadata.missing_attribute", operator
                                .getParameterAsString(SingleAttributeFilter.PARAMETER_ATTRIBUTE)));
                    }
                } else if (condition instanceof SubsetAttributeFilter) {
                    LinkedList<String> shouldBeFound = new LinkedList<>(Arrays.asList(operator.getParameterAsString(
                            SubsetAttributeFilter.PARAMETER_ATTRIBUTES).split(
                            ATTRIBUTE_SEPARATOR_CHARACTER)));
                    if (!shouldBeFound.isEmpty()) {
                        // delete already collected attributes
                        for (AttributeMetaData att : resultSet.getAllAttributes()) {
                            if (shouldBeFound.contains(att.getName())) {
                                shouldBeFound.remove(att.getName());
                            }

                        }
                        // show error for missing attributes
                        if (!shouldBeFound.isEmpty()) {
                            // show suitable error
                            for (String attName : shouldBeFound) {
                                inPort.addError(new SimpleMetaDataError(Severity.ERROR, inPort,
                                        "pio.error.metadata.missing_attribute", attName));
                            }
                        }
                    }
                }
            }

            return resultSet;
        } catch (ConditionCreationException e) {
            return new ExampleSetMetaData();
        } catch (UndefinedParameterError e) {
            return new ExampleSetMetaData();
        }
    }

    /**
     * This method returns the meta data of an exampleset as if it would have been filtered. If
     * exceptions are thrown because of wrong or missing parameter settings, null will be returned.
     * Please keep in mind that the resulting ExampleSetMetaData are only a clone of the original
     * one. Changes must hence be performed on the orginial exampleSetMetaData object.
     */
    public ExampleSetMetaData getMetaDataSubset(ExampleSetMetaData metaData, boolean keepSpecialIfNotIncluded) {
        return getMetaDataSubset(metaData, keepSpecialIfNotIncluded, false);
    }

    private boolean isOfAllowedType(int attributeValueType) {
        boolean keep = false;
        for (int type : valueTypes) {
            if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attributeValueType, type)) {
                keep = true;
                break;
            }
        }
        return keep;
    }

    /**
     * Returns the example subset which has been filtered according to the parameter settings.
     *
     * @param parent                   the example set which should be filtered
     * @param keepSpecialIfNotIncluded keep the special attributes if they are note included
     * @param failOnMissing            throws an UserError if the result Set does not contain all specified attributes
     *                                 (only relevant for subset- and single attribute-conditions)
     * @return the filtered example set error
     * @throws UndefinedParameterError necessary parameter not defined
     * @throws UserError               condition could not be created or specified Attributes does not exists
     */
    public ExampleSet getSubset(ExampleSet parent, boolean keepSpecialIfNotIncluded, boolean failOnMissing)
            throws UndefinedParameterError, UserError {
        boolean includeSpecial = operator.getParameterAsBoolean(PARAMETER_INCLUDE_SPECIAL_ATTRIBUTES);
        ExampleSet clone = (ExampleSet) parent.clone();
        Set<Attribute> attributeSubset = getAttributeSubset(clone, keepSpecialIfNotIncluded, failOnMissing);
        Iterator<Attribute> iterator = clone.getAttributes().allAttributes();
        while (iterator.hasNext()) {
            Attribute currentAttribute = iterator.next();
            if (!attributeSubset.contains(currentAttribute)) {
                iterator.remove();
            } else if (includeSpecial) { // if special are included: Set them to regular
                AttributeRole currentRole = clone.getAttributes().getRole(currentAttribute);
                if (currentRole.isSpecial()) {
                    currentRole.changeToRegular();
                }
            }
        }
        return clone;
    }

    /**
     * Returns the example subset which has been filtered according to the parameter settings.
     *
     * @param parent                   the example set which should be filtered
     * @param keepSpecialIfNotIncluded keep the special attributes if they are note included
     * @return the filtered example set error
     * @throws UndefinedParameterError necessary parameter not defined
     * @throws UserError               condition could not be created or specified Attributes does not exists
     */
    public ExampleSet getSubset(ExampleSet parent, boolean keepSpecialIfNotIncluded) throws UndefinedParameterError,
            UserError {
        return getSubset(parent, keepSpecialIfNotIncluded, false);
    }

    /**
     * Returns a set of attributes containing the original (!) attributes of the given example set
     * which match the filter settings of the parameter. Please keep in mind that this method does
     * not clone the example set like the getSubset method. If you change the attributes in the
     * subset, you will alter them in the given example set.
     *
     * @param exampleSet               the original example set
     * @param keepSpecialIfNotIncluded keep the special attributes if they are not included
     * @param failOnMissing            throws an UserError if the result Set does not contain all specified attributes
     *                                 (only relevant for subset- and single attribute-conditions)
     * @return a set of attributes matching the filters
     * @throws UndefinedParameterError
     * @throws UserError
     */
    public Set<Attribute> getAttributeSubset(ExampleSet exampleSet, boolean keepSpecialIfNotIncluded, boolean failOnMissing)
            throws UndefinedParameterError, UserError {
        try {
//			boolean includeSpecial = operator.getParameterAsBoolean(PARAMETER_INCLUDE_SPECIAL_ATTRIBUTES);
            boolean includeSpecial = false;
            Attributes attributes = exampleSet.getAttributes();
//			AttributeFilterCondition condition = createCondition(
//					CONDITION_NAMES[operator.getParameterAsInt(PARAMETER_FILTER_TYPE)], operator);
            AttributeFilterCondition condition = createCondition(
                    CONDITION_NAMES[PARAMETER_DEFAULT_FILTER_TYPE], operator);

            // adding attributes passing non scan check
//			boolean invert = operator.getParameterAsBoolean(PARAMETER_INVERT_SELECTION);
            boolean invert = false;
            Set<Attribute> remainingAttributes = new LinkedHashSet<>();

            // use iterator for including special attributes
            Iterator<Attribute> iterator = null;
            if (includeSpecial) {
                iterator = attributes.allAttributes();
            } else {
                iterator = attributes.iterator();
            }

            // In case we should fail on missing attributes and the selection is inverted
            // check if the inverted attributes are available
            if (failOnMissing && invert) {
                checkAvailableAttributes(includeSpecial, condition, iterator);

                // refresh iterator instance
                if (includeSpecial) {
                    iterator = attributes.allAttributes();
                } else {
                    iterator = attributes.iterator();
                }
            }

            while (iterator.hasNext()) {
                Attribute attribute = iterator.next();
                // check if it is allowed anyway
                if (isOfAllowedType(attribute.getValueType())) {
                    ScanResult result = condition.beforeScanCheck(attribute).invert(invert);
                    switch (result) {
                        case KEEP:
                        case UNCHECKED:
                            remainingAttributes.add(attribute);
                            break;
                        case REMOVE:
                            break;
                    }
                }
            }

            // now checking for every example
            if (condition.isNeedingScan()) {
                Iterator<Attribute> r = remainingAttributes.iterator();
                while (r.hasNext()) {
                    Attribute attribute = r.next();
                    ScanResult afterScanResult = ScanResult.UNCHECKED;
                    // now iterates over all examples as long as unchecked is returned
                    for (Example example : exampleSet) {
                        ScanResult result = condition.check(attribute, example);
                        if (result != ScanResult.UNCHECKED) {
                            afterScanResult = result;
                            break;
                        }
                    }

                    // if still is unchecked, then attribute has never been removed or needs full
                    // scan
                    if (condition.isNeedingFullScan()) {
                        afterScanResult = condition.checkAfterFullScan();
                    } else {
                        // if not needing full scan and still unchecked then it has never been
                        // removed (=keep)
                        if (afterScanResult == ScanResult.UNCHECKED) {
                            afterScanResult = ScanResult.KEEP;
                        }
                    }

                    // now inverting, cannot be unchecked now
                    afterScanResult = afterScanResult.invert(invert);
                    if (afterScanResult == ScanResult.REMOVE) {
                        attributes.remove(attribute);
                    }
                }
            }

            if (keepSpecialIfNotIncluded && !includeSpecial) {
                Iterator<AttributeRole> roleIterator = attributes.allAttributeRoles();
                while (roleIterator.hasNext()) {
                    AttributeRole currentRole = roleIterator.next();
                    if (currentRole.isSpecial()) {
                        remainingAttributes.add(currentRole.getAttribute());
                    }
                }
            }

            // In case we should fail on missing attributes and the selection is not inverted
            // check if the selected attribute(s) are available after filtering
            if (failOnMissing && !invert) {
                checkAvailableAttributes(includeSpecial, condition, remainingAttributes.iterator());
            }

            return remainingAttributes;
        } catch (ConditionCreationException e) {
            Throwable cause = e.getCause();
            if (cause instanceof UserError) {
                throw (UserError) cause;
            } else {
                if (operator instanceof Operator) {
                    throw new UserError((Operator) operator, e, "pio.error.cannot_instantiate",
                            CONDITION_NAMES[PARAMETER_DEFAULT_FILTER_TYPE], e.getMessage());
                } else {
                    throw new UserError(null, e, "pio.error.cannot_instantiate",
                            CONDITION_NAMES[PARAMETER_DEFAULT_FILTER_TYPE],
                            e.getMessage());
                }
            }
        }
    }

    /**
     * Performs a check whether the attributes specified by a {@link SingleAttributeFilter} or a
     * {@link SubsetAttributeFilter} are available in the provided iterator.
     *
     * @param includeSpecial whether or not special attributes have been included by the selector
     * @param condition      the selection condition
     * @param iterator       the iterator which contains the attributes to check
     * @throws UndefinedParameterError in case a required parameter is not defined yet
     * @throws UserError               in case the selected attributes are not available
     */
    private void checkAvailableAttributes(boolean includeSpecial, AttributeFilterCondition condition,
                                          Iterator<Attribute> iterator) throws UndefinedParameterError, UserError {
        if (condition instanceof SingleAttributeFilter) {
            String shouldBeFound = operator.getParameterAsString(SingleAttributeFilter.PARAMETER_ATTRIBUTE);
            boolean throwError = true;
            while (iterator.hasNext()) {
                Attribute attribute = iterator.next();
                if (attribute.getName().equals(shouldBeFound)) {
                    throwError = false;
                }
            }
            if (throwError) {
                // if include special attributes is NOT selected
                // that might be the reason why it's not found
                String errorId = includeSpecial ? AttributeNotFoundError.ATTRIBUTE_NOT_FOUND : "pio.error.regular_attribute_not_exist";
                throw new AttributeNotFoundError((Operator) operator, errorId,
                        SingleAttributeFilter.PARAMETER_ATTRIBUTE, shouldBeFound);
            }
        } else if (condition instanceof SubsetAttributeFilter) {
            LinkedList<String> shouldBeFound = new LinkedList<>(Arrays.asList(operator.getParameterAsString(
                    SubsetAttributeFilter.PARAMETER_ATTRIBUTES).split(ATTRIBUTE_SEPARATOR_CHARACTER)));
            // remove possibly empty entries
            shouldBeFound.remove("");
            if (!shouldBeFound.isEmpty()) {
                while (iterator.hasNext()) {
                    Attribute attribute = iterator.next();
                    if (shouldBeFound.contains(attribute.getName())) {
                        shouldBeFound.remove(attribute.getName());
                    }
                }
                // show suitable error
                // if include special attributes is NOT selected
                // that might be the reason why it's not found
                String errorId;
                if (includeSpecial) {
                    errorId = "pio.error.attribute_not_exist";
                } else {
                    errorId = "pio.error.regular_attribute_not_exist";
                }
                switch (shouldBeFound.size()) {
                    case 0:
                        break;
                    default:
                        throw new AttributeNotFoundError((Operator) operator, errorId,
                                SubsetAttributeFilter.PARAMETER_ATTRIBUTES, shouldBeFound.get(0));
                }
            }
        }
    }

    /**
     * Returns a set of attributes containing the original (!) attributes of the given example set
     * which match the filter settings of the parameter. Please keep in mind that this method does
     * not clone the example set like the getSubset method. If you change the attributes in the
     * subset, you will alter them in the given example set.
     *
     * @param exampleSet               the original example set
     * @param keepSpecialIfNotIncluded keep the special attributes if they are not included
     * @return a set of attributes matching the filters
     * @throws UndefinedParameterError
     * @throws UserError
     */
    public Set<Attribute> getAttributeSubset(ExampleSet exampleSet, boolean keepSpecialIfNotIncluded)
            throws UndefinedParameterError, UserError {
        return getAttributeSubset(exampleSet, keepSpecialIfNotIncluded, false);
    }

    /**
     * Checks if the given name is the short name of a known condition and creates it. If the name
     * is not known, this method creates a new instance of className which must be an implementation
     * of {@link Condition} by calling its two argument constructor passing it the example set and
     * the parameter string.
     */
    public static AttributeFilterCondition createCondition(String name, ParameterHandler operator)
            throws ConditionCreationException {
        try {
            for (int i = 0; i < CONDITION_NAMES.length; i++) {
                if (CONDITION_NAMES[i].equals(name)) {
                    AttributeFilterCondition condition = (AttributeFilterCondition) CONDITION_IMPLEMENTATIONS[i]
                            .newInstance();
                    condition.init(operator);
                    return condition;
                }
            }
            throw new ConditionCreationException("Cannot find class '" + name + "'. Check your classpath.");
        } catch (IllegalAccessException e) {
            throw new ConditionCreationException("'" + name + "' cannot access two argument constructor " + name
                    + "(ExampleSet, String)!", e);
        } catch (InstantiationException e) {
            throw new ConditionCreationException(name + ": cannot create condition (" + e.getMessage() + ").", e);
        } catch (Throwable e) {
            throw new ConditionCreationException(name + ": cannot create condition ("
                    + (e.getCause() != null ? e.getCause().getMessage() : e.getMessage()) + ").", e);
        }
    }

    /**
     * This method allows registering filter conditions defined in plugins.
     */
    public static void registerCondition(String conditionName, Class<? extends AttributeFilterCondition> conditionClass) {
        String[] newConditionNames = new String[CONDITION_NAMES.length + 1];
        System.arraycopy(CONDITION_NAMES, 0, newConditionNames, 0, CONDITION_NAMES.length);
        newConditionNames[newConditionNames.length - 1] = conditionName;
        CONDITION_NAMES = newConditionNames;

        Class<?>[] newConditionClasses = new Class[CONDITION_IMPLEMENTATIONS.length + 1];
        System.arraycopy(CONDITION_IMPLEMENTATIONS, 0, newConditionClasses, 0, CONDITION_IMPLEMENTATIONS.length);
        newConditionClasses[newConditionClasses.length - 1] = conditionClass;
        CONDITION_IMPLEMENTATIONS = newConditionClasses;
    }

    /**
     * This method creates the parameter types needed to filter attributes from example sets.
     */
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = new LinkedList<>();
        ParameterType type = new ParameterTypeCategory(PARAMETER_FILTER_TYPE,
                io.sugo.pio.i18n.I18N.getMessage("pio.AttributeSubsetSelector.attribute_filter_type"),
                CONDITION_NAMES, 2);
        type.setHidden(true);
        types.add(type);

        for (int i = 0; i < CONDITION_IMPLEMENTATIONS.length; i++) {
            Collection<ParameterType> filterConditions;
            try {
                filterConditions = ((AttributeFilterCondition) CONDITION_IMPLEMENTATIONS[i].newInstance())
                        .getParameterTypes(operator, inPort, valueTypes);
                for (ParameterType conditionalType : filterConditions) {
                    types.add(conditionalType);
                    conditionalType.registerDependencyCondition(new EqualTypeCondition(operator, PARAMETER_FILTER_TYPE,
                            CONDITION_NAMES, true, i));
                }
                // can't do anything about it
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (IllegalArgumentException e) {
            } catch (SecurityException e) {
            }
        }

        // Only retain this type.
//        types.addAll(new SubsetAttributeFilter().getParameterTypes(operator, inPort, valueTypes));

        type = new ParameterTypeBoolean(PARAMETER_INVERT_SELECTION,
                io.sugo.pio.i18n.I18N.getMessage("pio.AttributeSubsetSelector.invert_selection"), false);
        type.setHidden(true);
        types.add(type);
        type = new ParameterTypeBoolean(
                PARAMETER_INCLUDE_SPECIAL_ATTRIBUTES,
                io.sugo.pio.i18n.I18N.getMessage("pio.AttributeSubsetSelector.include_special_attributes"),
                false);
        type.setHidden(true);
        types.add(type);

        return types;
    }

    /**
     * Get parameter types that only used in 'SubsetAttributeFilter'
     */
    public List<ParameterType> getSubsetAttributeFilterParamTypes() {
        return new SubsetAttributeFilter().getParameterTypes(operator, inPort, valueTypes);
    }

    public Precondition makePrecondition() {
        return new Precondition() {

            @Override
            public void assumeSatisfied() {
            }

            @Override
            public void check(MetaData metaData) {
                if (metaData instanceof ExampleSetMetaData) {
                    ExampleSetMetaData subsetMetaData = getMetaDataSubset((ExampleSetMetaData) metaData, false);
                    if (subsetMetaData.getAllAttributes().isEmpty()) {
                        SimpleMetaDataError error = new SimpleMetaDataError(Severity.WARNING, inPort,
                                "pio.error.metadata.attribute_selection_empty");
                        inPort.addError(error);
                    }
                }
            }

            @Override
            public String getDescription() {
                return "Example set matching at least one selected attribute.";
            }

            @Override
            public MetaData getExpectedMetaData() {
                return new ExampleSetMetaData();
            }

            @Override
            public boolean isCompatible(MetaData input, CompatibilityLevel level) {
                return ExampleSet.class.isAssignableFrom(input.getObjectClass());
            }

        };
    }
}
