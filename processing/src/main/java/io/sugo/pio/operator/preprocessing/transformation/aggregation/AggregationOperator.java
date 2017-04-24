package io.sugo.pio.operator.preprocessing.transformation.aggregation;

import com.google.common.base.Strings;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Attributes;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.table.AttributeFactory;
import io.sugo.pio.example.table.DataRowFactory;
import io.sugo.pio.example.table.DoubleArrayDataRow;
import io.sugo.pio.example.util.ExampleSetBuilder;
import io.sugo.pio.example.util.ExampleSets;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.operator.OperatorVersion;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.annotation.ResourceConsumptionEstimator;
import io.sugo.pio.operator.error.ProcessSetupError.Severity;
import io.sugo.pio.operator.preprocessing.AbstractDataProcessing;
import io.sugo.pio.operator.preprocessing.filter.ExampleFilter;
import io.sugo.pio.parameter.*;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
import io.sugo.pio.ports.metadata.AttributeMetaData;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.ports.metadata.SimpleMetaDataError;
import io.sugo.pio.tools.AttributeSubsetSelector;
import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.OperatorResourceConsumptionHandler;
import org.apache.commons.lang.StringUtils;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


/**
 * <p>
 * This operator creates a new example set from the input example set showing the results of
 * arbitrary aggregation functions (as SUM, COUNT etc. known from SQL). Before the values of
 * different rows are aggregated into a new row the rows might be grouped by the values of a
 * multiple attributes (similar to the group-by clause known from SQL). In this case a new line will
 * be created for each group.
 * </p>
 * <p>
 * <p>
 * Please note that the known HAVING clause from SQL can be simulated by an additional
 * {@link ExampleFilter} operator following this one.
 * </p>
 */
public class AggregationOperator extends AbstractDataProcessing {

    private static final String PARAMETER_ATTRIBUTES = "attributes";

    public static class AggregationTreeNode {

        private TreeMap<Object, AggregationTreeNode> childrenMap = null;
        private TreeMap<Object, LeafAggregationTreeNode> leafMap = null;

        public AggregationTreeNode getOrCreateChild(Object value) {
            // creating map dynamically to save allocated objects in case this won't be used
            if (childrenMap == null) {
                childrenMap = new TreeMap<>();
            }

            // searching entry and creating it if necessary
            AggregationTreeNode childNode = childrenMap.get(value);
            if (childNode == null) {
                childNode = new AggregationTreeNode();
                childrenMap.put(value, childNode);
            }
            return childNode;
        }

        public AggregationTreeNode getChild(Object value) {
            if (childrenMap != null) {
                return childrenMap.get(value);
            }
            return null;
        }

        public Set<Entry<Object, AggregationTreeNode>> getChilds() {
            return childrenMap.entrySet();
        }

        public LeafAggregationTreeNode getOrCreateLeaf(Object value, List<AggregationFunction> aggregationFunctions) {
            // creating map dynamically to save allocated objects in case this won't be used
            if (leafMap == null) {
                leafMap = new TreeMap<>();
            }

            // searching entry and creating it if necessary
            LeafAggregationTreeNode leafNode = leafMap.get(value);
            if (leafNode == null) {
                leafNode = new LeafAggregationTreeNode(aggregationFunctions);
                leafMap.put(value, leafNode);
            }
            return leafNode;
        }

        public LeafAggregationTreeNode getLeaf(Object value) {
            if (leafMap != null) {
                return leafMap.get(value);
            }
            return null;
        }

        public Set<Entry<Object, LeafAggregationTreeNode>> getLeaves() {
            return leafMap.entrySet();
        }

        public Collection<? extends Object> getValues() {
            if (childrenMap != null) {
                return childrenMap.keySet();
            }
            if (leafMap != null) {
                return leafMap.keySet();
            }
            return Collections.emptyList();
        }
    }

    public static class LeafAggregationTreeNode {

        private List<Aggregator> aggregators;

        /**
         * Creates a new {@link LeafAggregationTreeNode} for all the given
         * {@link AggregationFunction}s. For each function, one {@link Aggregator} will be created,
         * that will keep track of the current counted values.
         */
        public LeafAggregationTreeNode(List<AggregationFunction> aggregationFunctions) {
            aggregators = new ArrayList<>(aggregationFunctions.size());
            for (AggregationFunction function : aggregationFunctions) {
                aggregators.add(function.createAggregator());
            }
        }

        /**
         * This will count the given examples for all registered {@link Aggregator}s.
         */
        public void count(Example example) {
            for (Aggregator aggregator : aggregators) {
                aggregator.count(example);
            }
        }

        /**
         * This will count the given examples for all registered {@link Aggregator}s with the given
         * weight. If there's no weight attribute available, it is preferable to use the
         * {@link #count(Example)} method, as it might be more efficiently implemented.
         */
        public void count(Example example, double weight) {
            for (Aggregator aggregator : aggregators) {
                aggregator.count(example, weight);
            }
        }

        /**
         * This simply returns the list of all aggregators. They may be used for setting values
         * within the respective data row of the created example set.
         */
        public List<Aggregator> getAggregators() {
            return aggregators;
        }
    }

    public static final String PARAMETER_USE_DEFAULT_AGGREGATION = "use_default_aggregation";
    public static final String PARAMETER_DEFAULT_AGGREGATION_FUNCTION = "default_aggregation_function";
    public static final String PARAMETER_AGGREGATION_ATTRIBUTES = "aggregation_attributes";
    public static final String PARAMETER_AGGREGATION_FUNCTIONS = "aggregation_functions";
    public static final String PARAMETER_GROUP_BY_ATTRIBUTES = "group_by_attributes";
    public static final String PARAMETER_ONLY_DISTINCT = "only_distinct";
    public static final String PARAMETER_IGNORE_MISSINGS = "ignore_missings";
    public static final String PARAMETER_ALL_COMBINATIONS = "count_all_combinations";

    /* These two only remain for compatibility */
    public static final String GENERIC_GROUP_NAME = "group";
    public static final String GENERIC_ALL_NAME = "all";

    /*
     * Later from this version, no group attribute will be created if no attributes for groups were
     * selected. Also after this numerical attributes used for grouping will not be transformed into
     * nominal attributes anymore.
     */
    private static final OperatorVersion VERSION_5_1_6 = new OperatorVersion(5, 1, 6);
    private static final OperatorVersion VERSION_5_2_8 = new OperatorVersion(5, 2, 8);
    /*
     * From version 6.0.7 on the operator will throw an user error if group-by-attributes argument
     * refers to an attribute not present in the example set or is empty
     */
    private static final OperatorVersion VERSION_6_0_6 = new OperatorVersion(6, 0, 6);

    private final AttributeSubsetSelector attributeSelector = new AttributeSubsetSelector(this, getExampleSetInputPort());

    public AggregationOperator() {
        super();
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.AggregationOperator.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.aggregation;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.AggregationOperator.description");
    }

    @Override
    public int getSequence() {
        return 0;
    }

    @Override
    protected MetaData modifyMetaData(ExampleSetMetaData metaData) throws UndefinedParameterError {
        ExampleSetMetaData resultMD = metaData.clone();
        resultMD.clear();

        // add group by attributes
        if (isParameterSet(PARAMETER_GROUP_BY_ATTRIBUTES)
                && !getParameterAsString(PARAMETER_GROUP_BY_ATTRIBUTES).isEmpty()) {
            String attributeRegex = getParameterAsString(PARAMETER_GROUP_BY_ATTRIBUTES);
            Pattern pattern = Pattern.compile(attributeRegex);

            for (AttributeMetaData amd : metaData.getAllAttributes()) {
                if (pattern.matcher(amd.getName()).matches()) {
                    /*if (amd.isNumerical() && getCompatibilityLevel().isAtMost(VERSION_5_1_6)) {
						// converting type to mimic NumericalToPolynomial used below
						amd.setType(Ontology.NOMINAL);
						amd.setValueSet(Collections.<String> emptySet(), SetRelation.SUPERSET);
					}*/
                    resultMD.addAttribute(amd);
                }
            }
            resultMD.getNumberOfExamples().reduceByUnknownAmount();
        }
		/*if (resultMD.getAllAttributes().isEmpty() && getCompatibilityLevel().isAtMost(VERSION_5_1_6)) {
			AttributeMetaData allGroup = new AttributeMetaData(GENERIC_GROUP_NAME, Ontology.NOMINAL);
			Set<String> values = new TreeSet<>();
			values.add(GENERIC_ALL_NAME);
			allGroup.setValueSet(values, SetRelation.EQUAL);
			resultMD.addAttribute(allGroup);
			resultMD.setNumberOfExamples(new MDInteger(1));
		}*/

        // add aggregated attributes of default aggregation: They will apply only to those attribute
        // not mentioned explicitly
        List<String[]> parameterList = this.getParameterList(PARAMETER_AGGREGATION_ATTRIBUTES);
        HashSet<String> explicitDefinedAttributes = new HashSet<>();
        for (String[] function : parameterList) {
            explicitDefinedAttributes.add(function[0]);
        }
        if (getParameterAsBoolean(PARAMETER_USE_DEFAULT_AGGREGATION)) {
            String defaultFunction = getParameterAsString(PARAMETER_DEFAULT_AGGREGATION_FUNCTION);
            ExampleSetMetaData metaDataSubset = attributeSelector.getMetaDataSubset(metaData, false);
            for (AttributeMetaData amd : metaDataSubset.getAllAttributes()) {
                if (!explicitDefinedAttributes.contains(amd.getName())) {
                    AttributeMetaData newAMD = AggregationFunction.getAttributeMetaData(defaultFunction, amd,
                            getExampleSetInputPort());
                    if (newAMD != null) {
                        resultMD.addAttribute(newAMD);
                    }
                }
            }
        }

        // add explicitly defined attributes of list
        for (String[] function : parameterList) {
            AttributeMetaData amd = metaData.getAttributeByName(function[0]);
            if (amd != null) {
                AttributeMetaData newMD = AggregationFunction.getAttributeMetaData(function[1], amd,
                        getExampleSetInputPort());
                if (newMD != null) {
                    resultMD.addAttribute(newMD);
                }
            } else {
                // in this case we should register a warning, but continue anyway in cases we don't
                // have the correct set available
                getExampleSetInputPort().addError(new SimpleMetaDataError(Severity.WARNING, getExampleSetInputPort(),
                        "aggregation.attribute_unknown", function[0]));
                AttributeMetaData newAMD = AggregationFunction.getAttributeMetaData(function[1],
                        new AttributeMetaData(function[0], Ontology.ATTRIBUTE_VALUE), getExampleSetInputPort());
                if (newAMD != null) {
                    resultMD.addAttribute(newAMD);
                }
            }
        }

//		if (getCompatibilityLevel().isAbove(VERSION_6_0_6)) {
        String groupByAttributesStr = getParameterAsString(PARAMETER_GROUP_BY_ATTRIBUTES);
        if (!Strings.isNullOrEmpty(groupByAttributesStr)) {
            String[] groupByAttributes = groupByAttributesStr.split(ParameterTypeAttributes.ATTRIBUTE_SEPARATOR_REGEX);
            for (String attribute : groupByAttributes) {
                // if the list is empty, there is already a warning:
                if (!attribute.isEmpty() && metaData.getAttributeByName(attribute) == null) {
                    getExampleSetInputPort().addError(new SimpleMetaDataError(Severity.WARNING, getExampleSetInputPort(),
                            "aggregation.group_by_attribute_unknown", attribute));
                }
            }
        }

//		}

        return resultMD;
    }

    @Override
    public ExampleSet apply(ExampleSet exampleSet) throws OperatorException {
        // creating data structures for building aggregates
        collectLog("Creating data structures for building aggregates.");
        List<AggregationFunction> aggregationFunctions = createAggreationFunctions(exampleSet);
        Attribute[] groupAttributes;

		/*
		 * From version 6.0.7 on, the group-by-attribute parameter must contain valid attribute
		 * names (e.g. the names must be presented in the example set and do not contain invalid
		 * characters like "|").
		 */
//		if (getCompatibilityLevel().isAbove(VERSION_6_0_6)) {
        String[] groupByAttributesNames = getParameterAsString(PARAMETER_GROUP_BY_ATTRIBUTES)
                .split(ParameterTypeAttributes.ATTRIBUTE_SEPARATOR_REGEX);

        // if the group-by-attributes parameter is empty, we can skip these checks
        boolean emptySelection = groupByAttributesNames.length == 1 && groupByAttributesNames[0].trim().isEmpty();
        if (emptySelection) {
            groupAttributes = new Attribute[0];
        } else {
            List<String> unknownAttributes = new ArrayList<>();
            Set<Attribute> groupByAttributes = new HashSet<>(groupByAttributesNames.length);

            for (String attributeName : groupByAttributesNames) {
                // the user can add empty attribute names by accident
                if (attributeName.trim().length() == 0) {
                    continue;
                }
                Attribute attribute = exampleSet.getAttributes().get(attributeName);
                if (attribute == null) {
                    unknownAttributes.add(attributeName);
                } else {
                    groupByAttributes.add(attribute);
                }
            }

            if (unknownAttributes.size() != 0) {
                if (unknownAttributes.size() > 1) {
                    throw new UserError(this, "aggregate_group_by_not_found", StringUtils.join(unknownAttributes, ", "));
                } else {
                    throw new UserError(this, "aggregate_group_by_not_found_single", unknownAttributes.get(0));
                }
            } else {
                // keep the attribute ordering of the input example set (to conform with the
                // legacy code)
                groupAttributes = new Attribute[groupByAttributes.size()];
                Iterator<Attribute> it = exampleSet.getAttributes().allAttributes();
                int i = 0;
                while (it.hasNext()) {
                    Attribute attribute = it.next();
                    if (groupByAttributes.contains(attribute)) {
                        groupAttributes[i] = attribute;
                        i++;
                    }
                }
            }
        }
		/*} else {
			groupAttributes = getMatchingAttributes(exampleSet.getAttributes(),
					getParameterAsString(PARAMETER_GROUP_BY_ATTRIBUTES));
		}*/

        Attribute weightAttribute = exampleSet.getAttributes().getWeight();
        boolean useWeights = weightAttribute != null;

        // running over exampleSet and aggregate data of each example
        collectLog("Running over example set and aggregate data of each example.");
        AggregationTreeNode rootNode = new AggregationTreeNode();
        LeafAggregationTreeNode leafNode = null;
        if (groupAttributes.length == 0) {
            // if no grouping, we will directly insert into leaf node
            leafNode = new LeafAggregationTreeNode(aggregationFunctions);
        }
        getProgress().setTotal(exampleSet.size());
        int progressCounter = 0;
        for (Example example : exampleSet) {
            if (groupAttributes.length > 0) {
                AggregationTreeNode currentNode = rootNode;
                // now traversing aggregation tree for m-1 group attributes
                for (int i = 0; i < groupAttributes.length - 1; i++) {
                    Attribute currentAttribute = groupAttributes[i];
                    if (currentAttribute.isNominal()) {
                        currentNode = currentNode.getOrCreateChild(example.getValueAsString(currentAttribute));
                    } else {
                        currentNode = currentNode.getOrCreateChild(example.getValue(currentAttribute));
                    }
                }

                // now we have to get the leaf node containing the aggregators
                Attribute currentAttribute = groupAttributes[groupAttributes.length - 1];
                if (currentAttribute.isNominal()) {
                    leafNode = currentNode.getOrCreateLeaf(example.getValueAsString(currentAttribute), aggregationFunctions);
                } else {
                    leafNode = currentNode.getOrCreateLeaf(example.getValue(currentAttribute), aggregationFunctions);
                }
            }
            // now count current example
            if (!useWeights) {
                leafNode.count(example);
            } else {
                leafNode.count(example, example.getValue(weightAttribute));
            }

            // Trigger operator progress
            if (++progressCounter % 100 == 0) {
                getProgress().setCompleted(progressCounter);
            }
        }

        // now derive new example set from aggregated values
        collectLog("Derive new example set from aggregated values.");
        boolean isCountingAllCombinations = getParameterAsBoolean(PARAMETER_ALL_COMBINATIONS);

        // building new attributes from grouping attributes and aggregation functions
        collectLog("Building new attributes from grouping attributes and aggregation functions.");
        Attribute[] newAttributes = new Attribute[groupAttributes.length + aggregationFunctions.size()];
        for (int i = 0; i < groupAttributes.length; i++) {
            newAttributes[i] = AttributeFactory.createAttribute(groupAttributes[i]);
        }
        int i = groupAttributes.length;
        for (AggregationFunction function : aggregationFunctions) {
            newAttributes[i] = function.getTargetAttribute();
            i++;
        }

        // creating example set
        ExampleSetBuilder builder = ExampleSets.from(newAttributes);
        DataRowFactory factory = new DataRowFactory(DataRowFactory.TYPE_DOUBLE_ARRAY, '.');
        double[] dataOfUpperLevels = new double[groupAttributes.length];

        // prepare empty lists
        ArrayList<List<Aggregator>> allAggregators = new ArrayList<>();
        for (int aggregatorIdx = 0; aggregatorIdx < aggregationFunctions.size(); ++aggregatorIdx) {
            allAggregators.add(new ArrayList<Aggregator>());
        }

        ArrayList<double[]> allGroupCombinations = new ArrayList<>();

        if (groupAttributes.length > 0) {
            // going through all possible groups recursively
            parseTree(rootNode, groupAttributes, dataOfUpperLevels, 0, allGroupCombinations, allAggregators, factory,
                    newAttributes, isCountingAllCombinations, aggregationFunctions);
        } else {
            // just enter values from single leaf node
            parseLeaf(leafNode, dataOfUpperLevels, allGroupCombinations, allAggregators, factory, newAttributes,
                    aggregationFunctions);
        }

        // apply post-processing
        int currentFunctionIdx = 0;
        for (AggregationFunction aggregationFunction : aggregationFunctions) {
            aggregationFunction.postProcessing(allAggregators.get(currentFunctionIdx));
            ++currentFunctionIdx;
        }

        // write data into table
        collectLog("Write data into table.");
        builder.withExpectedSize(allGroupCombinations.size());
        int currentRow = 0;
        for (double[] groupValues : allGroupCombinations) {
            double[] rowData = new double[newAttributes.length];

            // copy group values into row
            System.arraycopy(groupValues, 0, rowData, 0, groupValues.length);
            DoubleArrayDataRow dataRow = new DoubleArrayDataRow(rowData);

            // copy aggregated values into row
            int currentColumn = groupValues.length;
            for (List<Aggregator> aggregatorsForColumn : allAggregators) {
                Aggregator aggregatorForCurrentCell = aggregatorsForColumn.get(currentRow);
                Attribute currentAttribute = newAttributes[currentColumn];
                if (aggregatorForCurrentCell != null) {
                    aggregatorForCurrentCell.set(currentAttribute, dataRow);
                } else {
                    aggregationFunctions.get(currentColumn - groupAttributes.length).setDefault(currentAttribute, dataRow);
                }
                ++currentColumn;
            }
            builder.addDataRow(dataRow);
            ++currentRow;
        }

        // postprocessing for remaining compatibility: Old versions automatically added group "all".
        // Must remain this way for old operator
        // version
		/*if (getCompatibilityLevel().isAtMost(VERSION_5_1_6)) {
			if (groupAttributes.length == 0) {
				ExampleSet resultSet = builder.build();
				Attribute resultGroupAttribute = AttributeFactory.createAttribute(GENERIC_GROUP_NAME, Ontology.NOMINAL);
				resultSet.getExampleTable().addAttribute(resultGroupAttribute);
				resultSet.getAttributes().addRegular(resultGroupAttribute);
				resultSet.getExample(0).setValue(resultGroupAttribute,
						resultGroupAttribute.getMapping().mapString(GENERIC_ALL_NAME));

				resultSet.getAnnotations().addAll(exampleSet.getAnnotations());
				for (Attribute attribute : newAttributes) {
					resultSet.getAttributes().remove(attribute);
					resultSet.getAttributes().addRegular(attribute);
				}
				return resultSet;
			} else {
				// make attributes nominal
				ExampleSet resultSet = builder.build();
				resultSet.getAnnotations().addAll(exampleSet.getAnnotations());
				try {
					NumericToNominal toNominalOperator = OperatorService.createOperator(NumericToPolynominal.class);
					toNominalOperator.setParameter(AttributeSubsetSelector.PARAMETER_FILTER_TYPE,
							AttributeSubsetSelector.CONDITION_REGULAR_EXPRESSION + "");
					toNominalOperator.setParameter(RegexpAttributeFilter.PARAMETER_REGULAR_EXPRESSION,
							getParameterAsString(PARAMETER_GROUP_BY_ATTRIBUTES));
					toNominalOperator.setParameter(AttributeSubsetSelector.PARAMETER_INCLUDE_SPECIAL_ATTRIBUTES, "true");
					return toNominalOperator.apply(resultSet);
				} catch (OperatorCreationException e) {
					// otherwise compatibility could not be ensured
					return resultSet;
				}
			}
		}*/

        // for recent version table is correct: Deliver example set
        ExampleSet resultSet = builder.build();
        resultSet.getAnnotations().addAll(exampleSet.getAnnotations());
        return resultSet;
    }

    private void parseLeaf(LeafAggregationTreeNode node, double[] dataOfUpperLevels, List<double[]> allGroupCombinations,
                           List<List<Aggregator>> allAggregators, DataRowFactory factory, Attribute[] newAttributes,
                           List<AggregationFunction> aggregationFunctions) {
        // first copying data from groups
        double[] newGroupCombination = new double[dataOfUpperLevels.length];
        System.arraycopy(dataOfUpperLevels, 0, newGroupCombination, 0, dataOfUpperLevels.length);
        allGroupCombinations.add(newGroupCombination);

        // DoubleArrayDataRow row = new DoubleArrayDataRow(newData);

        // check whether leaf exists
        if (node != null) {
            // int i = dataOfUpperLevels.length; // number of group attributes
            int i = 0;
            for (Aggregator aggregator : node.getAggregators()) {
                allAggregators.get(i).add(aggregator);
                // aggregator.set(newAttributes[i], row);
                i++;
            }
        } else {
            // fill in defaults for all aggregation functions
            // int i = dataOfUpperLevels.length; // number of group attributes
            // for (AggregationFunction function : aggregationFunctions) {
            // function.setDefault(newAttributes[i], row);
            // i++;
            for (List<Aggregator> current : allAggregators) {
                current.add(null);
            }
        }

        // table.addDataRow(row);
    }

    private void parseTree(AggregationTreeNode node, Attribute[] groupAttributes, double[] dataOfUpperLevels, int groupLevel,
                           List<double[]> allGroupCombinations, List<List<Aggregator>> allAggregators, DataRowFactory factory,
                           Attribute[] newAttributes, boolean isCountingAllCombinations, List<AggregationFunction> aggregationFunctions)
            throws UserError {
        Attribute currentAttribute = groupAttributes[groupLevel];
        if (currentAttribute.isNominal()) {
            Collection<? extends Object> nominalValues = null;
            if (isCountingAllCombinations) {
                nominalValues = currentAttribute.getMapping().getValues();
            } else {
                nominalValues = node.getValues();
            }
            for (Object nominalValue : nominalValues) {
                dataOfUpperLevels[groupLevel] = newAttributes[groupLevel].getMapping().mapString(nominalValue.toString());
                // check if we have more group defining attributes
                if (groupLevel + 1 < groupAttributes.length) {
                    parseTree(node.getOrCreateChild(nominalValue), groupAttributes, dataOfUpperLevels, groupLevel + 1,
                            allGroupCombinations, allAggregators, factory, newAttributes, isCountingAllCombinations,
                            aggregationFunctions);
                } else {
                    // if not, insert values from aggregation functions
                    parseLeaf(node.getLeaf(nominalValue), dataOfUpperLevels, allGroupCombinations, allAggregators, factory,
                            newAttributes, aggregationFunctions);
                }

            }
        } else if (currentAttribute.isNumerical()
                || Ontology.ATTRIBUTE_VALUE_TYPE.isA(currentAttribute.getValueType(), Ontology.DATE_TIME)) {
            for (Object numericalValue : node.getValues()) {
                dataOfUpperLevels[groupLevel] = (Double) numericalValue;
                if (groupLevel + 1 < groupAttributes.length) {
                    parseTree(node.getOrCreateChild(numericalValue), groupAttributes, dataOfUpperLevels, groupLevel + 1,
                            allGroupCombinations, allAggregators, factory, newAttributes, isCountingAllCombinations,
                            aggregationFunctions);
                } else {
                    // if not, insert values from aggregation functions
                    parseLeaf(node.getLeaf(numericalValue), dataOfUpperLevels, allGroupCombinations, allAggregators, factory,
                            newAttributes, aggregationFunctions);
                }
            }
        } else {
            throw new UserError(this, "aggregation_operator.unsupported_value_type", currentAttribute.getName(),
                    Ontology.ATTRIBUTE_VALUE_TYPE.getNames()[currentAttribute.getValueType()]);
        }
    }

    private Attribute[] getMatchingAttributes(Attributes attributes, String regex) throws OperatorException {
        Pattern pattern = null;
        try {
            pattern = Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            throw new UserError(this, "pio.error.regular_expression_error", regex, e.getMessage());
        }
        List<Attribute> attributeList = new LinkedList<>();
        Iterator<Attribute> iterator = attributes.allAttributes();
        while (iterator.hasNext()) {
            Attribute attribute = iterator.next();
            if (pattern.matcher(attribute.getName()).matches()) {
                attributeList.add(attribute);
            }
        }

        // building array of attributes for faster access.
        Attribute[] attributesArray = new Attribute[attributeList.size()];
        attributesArray = attributeList.toArray(attributesArray);
        return attributesArray;
    }

    private List<AggregationFunction> createAggreationFunctions(ExampleSet exampleSet) throws OperatorException {
        // load global switches
        boolean ignoreMissings = getParameterAsBoolean(PARAMETER_IGNORE_MISSINGS);
        boolean countOnlyDistinct = getParameterAsBoolean(PARAMETER_ONLY_DISTINCT);

        // creating data structures for building aggregates
        List<AggregationFunction> aggregationFunctions = new LinkedList<>();

        // building functions for all explicitly defined aggregation attributes
        Set<Attribute> explicitlyAggregatedAttributes = new HashSet<>();
        List<String[]> aggregationFunctionPairs = getParameterList(PARAMETER_AGGREGATION_ATTRIBUTES);
        for (String[] aggregationFunctionPair : aggregationFunctionPairs) {
            Attribute attribute = exampleSet.getAttributes().get(aggregationFunctionPair[0]);
            if (attribute == null) {
                throw new UserError(this, "aggregation.aggregation_attribute_not_present", aggregationFunctionPair[0]);
            }
            AggregationFunction function = AggregationFunction.createAggregationFunction(aggregationFunctionPair[1],
                    attribute, ignoreMissings, countOnlyDistinct);
            if (!function.isCompatible()) {
                throw new UserError(this, "aggregation.incompatible_attribute_type", attribute.getName(),
                        aggregationFunctionPair[1]);
            }
            // adding objects for this attribute to structure
            explicitlyAggregatedAttributes.add(attribute);
            aggregationFunctions.add(function);
        }

        // building the default aggregations
        if (getParameterAsBoolean(PARAMETER_USE_DEFAULT_AGGREGATION)) {
            String defaultAggregationFunctionName = getParameterAsString(PARAMETER_DEFAULT_AGGREGATION_FUNCTION);

            Iterator<Attribute> iterator = attributeSelector.getAttributeSubset(exampleSet, false).iterator();
			/*if (getCompatibilityLevel().isAtMost(VERSION_5_2_8)) {
				iterator = exampleSet.getAttributes().iterator();
			}*/

            while (iterator.hasNext()) {
                Attribute attribute = iterator.next();
                if (!explicitlyAggregatedAttributes.contains(attribute)) {
                    AggregationFunction function = AggregationFunction.createAggregationFunction(
                            defaultAggregationFunctionName, attribute, ignoreMissings, countOnlyDistinct);
                    if (function.isCompatible()) {
                        aggregationFunctions.add(function);
                    }
                }
            }
        }

        return aggregationFunctions;
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        types.add(new ParameterTypeBoolean(PARAMETER_USE_DEFAULT_AGGREGATION,
                I18N.getMessage("pio.AggregationOperator.use_default_aggregation"), true, false, true));
        List<ParameterType> parameterTypes = attributeSelector.getParameterTypes();
        for (ParameterType type : parameterTypes) {
            type.registerDependencyCondition(
                    new BooleanParameterCondition(this, PARAMETER_USE_DEFAULT_AGGREGATION, false, true));
            types.add(type);
        }
        String[] functions = AggregationFunction.getAvailableAggregationFunctionNames();
        ParameterType type = new ParameterTypeStringCategory(PARAMETER_DEFAULT_AGGREGATION_FUNCTION,
                I18N.getMessage("pio.AggregationOperator.default_aggregation_function"), functions, functions[0]);
        type.registerDependencyCondition(
                new BooleanParameterCondition(this, PARAMETER_USE_DEFAULT_AGGREGATION, false, true));
        type.setExpert(false);
        types.add(type);

        /*types.add(new ParameterTypeList(PARAMETER_AGGREGATION_ATTRIBUTES, I18N.getMessage("pio.AggregationOperator.aggregation_attributes"),
                new ParameterTypeAttribute("aggregation_attribute", I18N.getMessage("pio.AggregationOperator.aggregation_attributes"),
                        getExampleSetInputPort()),
                new ParameterTypeStringCategory(PARAMETER_AGGREGATION_FUNCTIONS,
                        I18N.getMessage("pio.AggregationOperator.aggregation_functions"), functions, functions[0])));*/
        types.add(new ParameterTypeAttributes(PARAMETER_GROUP_BY_ATTRIBUTES,
                I18N.getMessage("pio.AggregationOperator.group_by_attributes"), getExampleSetInputPort(),
                true));
        /*types.add(new ParameterTypeBoolean(PARAMETER_ALL_COMBINATIONS,
                I18N.getMessage("pio.AggregationOperator.count_all_combinations"),
                false));
        type = new ParameterTypeBoolean(PARAMETER_ONLY_DISTINCT,
                I18N.getMessage("pio.AggregationOperator.only_distinct"),
                false);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_ALL_COMBINATIONS, false, false));
        types.add(type);*/
        types.add(new ParameterTypeBoolean(PARAMETER_IGNORE_MISSINGS,
                I18N.getMessage("pio.AggregationOperator.ignore_missings"),
                true, false, true));

        types.forEach(parameterType -> {
            if (PARAMETER_ATTRIBUTES.equals(parameterType.getKey())) {
                parameterType.setDescription(I18N.getMessage("pio.AggregationOperator.attributes_desc"));
                parameterType.setFullName(I18N.getMessage("pio.AggregationOperator.attributes_desc"));
            }
        });
        return types;
    }

	/*@Override
	public OperatorVersion[] getIncompatibleVersionChanges() {
		return (OperatorVersion[]) ArrayUtils.addAll(super.getIncompatibleVersionChanges(),
				new OperatorVersion[] { VERSION_5_1_6, VERSION_5_2_8, VERSION_6_0_6 });
	}*/

    @Override
    public boolean writesIntoExistingData() {
        return false;
    }

    //	@Override
    public ResourceConsumptionEstimator getResourceConsumptionEstimator() {
        return OperatorResourceConsumptionHandler.getResourceConsumptionEstimator(getInputPort(), AggregationOperator.class,
                null);
    }
}
