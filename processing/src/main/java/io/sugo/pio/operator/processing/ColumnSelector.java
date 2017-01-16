package io.sugo.pio.operator.processing;

import io.sugo.pio.example.*;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.processing.filter.*;
import io.sugo.pio.parameter.ParameterHandler;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.tools.Ontology;

import java.util.*;

/**
 * Created by root on 17-1-13.
 */
public class ColumnSelector extends AbstractProcessing {

    public static final String PARAMETER_FILTER_TYPE = "attribute_filter_type";
    public static final String PARAMETER_INVERT_SELECTION = "invert_selection";
    public static final String PARAMETER_INCLUDE_SPECIAL_ATTRIBUTES = "include_special_attributes";
    public static String[] CONDITION_NAMES = new String[]{"all", "single", "subset", "regular_expression", "value_type",
            "block_type", "no_missing_values", "numeric_value_filter"};
    private static Class<?>[] CONDITION_IMPLEMENTATIONS = {TransparentAttributeFilter.class, SingleAttributeFilter.class,
            SubsetAttributeFilter.class, RegexpAttributeFilter.class, ValueTypeAttributeFilter.class,
            BlockTypeAttributeFilter.class, NoMissingValuesAttributeFilter.class, NumericValueAttributeFilter.class};

    private int[] valueTypes;

    public ColumnSelector(String name, int... valueTypes) {
        super(name);
        if (valueTypes.length == 0) {
            this.valueTypes = new int[]{Ontology.ATTRIBUTE_VALUE};
        } else {
            this.valueTypes = valueTypes;
        }
    }

    @Override
    public ExampleSet apply(ExampleSet exampleSet) {
        Attributes attributes = exampleSet.getAttributes();
        Set<Attribute> attributeSubset = getAttributeSubset(exampleSet, true);
        Iterator<Attribute> r = attributes.allAttributes();
        while (r.hasNext()) {
            Attribute attribute = r.next();
            if (!attributeSubset.contains(attribute)) {
                r.remove();
            }
        }
        return exampleSet;
    }

    public Set<Attribute> getAttributeSubset(ExampleSet exampleSet, boolean keepSpecialIfNotIncluded)
            throws UndefinedParameterError, UserError {
        return getAttributeSubset(exampleSet, keepSpecialIfNotIncluded, false);
    }

    public Set<Attribute> getAttributeSubset(ExampleSet exampleSet, boolean keepSpecialIfNotIncluded, boolean failOnMissing)
            throws UndefinedParameterError, UserError {
        try {
            boolean includeSpecial = getParameterAsBoolean(PARAMETER_INCLUDE_SPECIAL_ATTRIBUTES);
            Attributes attributes = exampleSet.getAttributes();
            AttributeFilterCondition condition = createCondition(
                    CONDITION_NAMES[getParameterAsInt(PARAMETER_FILTER_TYPE)], this);

            // adding attributes passing non scan check
            boolean invert = getParameterAsBoolean(PARAMETER_INVERT_SELECTION);
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
                throw new UserError(this, e, 904,
                        CONDITION_NAMES[getParameterAsInt(PARAMETER_FILTER_TYPE)], e.getMessage());
            }
        }
    }

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

    private void checkAvailableAttributes(boolean includeSpecial, AttributeFilterCondition condition,
                                          Iterator<Attribute> iterator) throws UndefinedParameterError, UserError {
        if (condition instanceof SingleAttributeFilter) {
            String shouldBeFound = getParameterAsString(SingleAttributeFilter.PARAMETER_ATTRIBUTE);
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
                int errorNumber = includeSpecial ? AttributeNotFoundError.ATTRIBUTE_NOT_FOUND : 164;
                throw new AttributeNotFoundError(this, errorNumber,
                        SingleAttributeFilter.PARAMETER_ATTRIBUTE, shouldBeFound);
            }
        } else if (condition instanceof SubsetAttributeFilter) {
            LinkedList<String> shouldBeFound = new LinkedList<>(Arrays.asList(getParameterAsString(
                    SubsetAttributeFilter.PARAMETER_ATTRIBUTES).split(SubsetAttributeFilter.PARAMETER_ATTRIBUTES_SEPERATOR)));
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
                int errorNumber;
                if (includeSpecial) {
                    errorNumber = 160;
                } else {
                    errorNumber = 164;
                }
                switch (shouldBeFound.size()) {
                    case 0:
                        break;
                    default:
                        throw new AttributeNotFoundError(this, errorNumber,
                                SubsetAttributeFilter.PARAMETER_ATTRIBUTES, shouldBeFound.get(0));
                }
            }
        }
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
}
