package io.sugo.pio.operator.learner.associations.fpgrowth;

import com.metamx.common.logger.Logger;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Tools;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.*;
import io.sugo.pio.operator.io.ExampleSource;
import io.sugo.pio.operator.learner.associations.BooleanAttributeItem;
import io.sugo.pio.operator.learner.associations.FrequentItemSet;
import io.sugo.pio.operator.learner.associations.FrequentItemSets;
import io.sugo.pio.operator.learner.associations.Item;
import io.sugo.pio.parameter.*;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.metadata.ExampleSetPrecondition;
import io.sugo.pio.tools.Ontology;

import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * <p>
 * This operator calculates all frequent items sets from a data set by building a FPTree data
 * structure on the transaction data base. This is a very compressed copy of the data which in many
 * cases fits into main memory even for large data bases. From this FPTree all frequent item set are
 * derived. A major advantage of FPGrowth compared to Apriori is that it uses only 2 data scans and
 * is therefore often applicable even on large data sets.
 * </p>
 * <p>
 * <p>
 * Please note that the given data set is only allowed to contain binominal attributes, i.e. nominal
 * attributes with only two different values. Simply use the provided preprocessing operators in
 * order to transform your data set. The necessary operators are the discretization operators for
 * changing the value types of numerical attributes to nominal and the operator Nominal2Binominal
 * for transforming nominal attributes into binominal / binary ones.
 * </p>
 * <p>
 * <p>
 * The frequent item sets are mined for the positive entries in your data base, i.e. for those
 * nominal values which are defined as positive in your data base. If you use an attribute
 * description file (.aml) for the {@link ExampleSource} operator this corresponds to the second
 * value which is defined via the classes attribute or inner value tags.
 * </p>
 * <p>
 * <p>
 * If your data does not specify the positive entries correctly, you may set them using the
 * parameter positive_value. This only works if all your attributes contain this value!
 * </p>
 * <p>
 * <p>
 * This operator has two basic working modes: finding at least the specified number of item sets
 * with highest support without taking the min_support into account (default) or finding all item
 * sets with a support large than min_support.
 * </p>
 */
public class FPGrowth extends Operator {

    private static final Logger logger = new Logger(FPGrowth.class);

    /**
     * Indicates if this operator should try to find a minimum number of item sets by iteratively
     * decreasing the minimum support.
     */
    public static final String PARAMETER_FIND_MIN_NUMBER_OF_ITEMSETS = "find_min_number_of_itemsets";

    /**
     * Indicates the minimum number of item sets by iteratively decreasing the minimum support.
     */
    public static final String PARAMETER_MIN_NUMBER_OF_ITEMSETS = "min_number_of_itemsets";

    public static final String PARAMETER_MAX_REDUCTION_STEPS = "max_number_of_retries";

    public static final String PARAMETER_POSITIVE_VALUE = "positive_value";

    /**
     * The parameter name for &quot;Minimal Support&quot;
     */
    public static final String PARAMETER_MIN_SUPPORT = "min_support";

    /**
     * The parameter name the maximum number of items.
     */
    public static final String PARAMETER_MAX_ITEMS = "max_items";

    public static final String PARAMETER_MUST_CONTAIN = "must_contain";

    private static final String PARAMETER_KEEP_EXAMPLE_SET = "keep_example_set";

    private final InputPort exampleSetInput = getInputPorts().createPort("example set");

    private final OutputPort exampleSetOutput = getOutputPorts().createPort("example set");
    private final OutputPort frequentSetsOutput = getOutputPorts().createPort("frequent sets");

    public FPGrowth() {
        super();

        exampleSetInput.addPrecondition(new ExampleSetPrecondition(exampleSetInput, Ontology.BINOMINAL));
        getTransformer().addGenerationRule(frequentSetsOutput, FrequentItemSets.class);
        getTransformer().addPassThroughRule(exampleSetInput, exampleSetOutput);
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.FPGrowth.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.algorithmModel;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.FPGrowth.description");
    }

    @Override
    public void doWork() throws OperatorException {
        ExampleSet exampleSet = exampleSetInput.getData(ExampleSet.class);
        logger.info("FPGrowth begin to deal with example set, size[%d].", exampleSet.size());

        // check
        Tools.onlyNominalAttributes(exampleSet, "FPGrowth");

        boolean shouldFindMinimumNumber = getParameterAsBoolean(PARAMETER_FIND_MIN_NUMBER_OF_ITEMSETS);
        int maximalNumberOfRetries = shouldFindMinimumNumber ? getParameterAsInt(PARAMETER_MAX_REDUCTION_STEPS) : 1;
        int minimumNumberOfItemsets = shouldFindMinimumNumber ? getParameterAsInt(PARAMETER_MIN_NUMBER_OF_ITEMSETS) : 1;

        logger.info("FPGrowth parameter 'maximalNumberOfRetries':%d.", maximalNumberOfRetries);
        logger.info("FPGrowth parameter 'minimumNumberOfItemsets':%d.", minimumNumberOfItemsets);

        int maxItems = getParameterAsInt(PARAMETER_MAX_ITEMS);
        double currentSupport = getParameterAsDouble(PARAMETER_MIN_SUPPORT);

        // determine frequent items sets
        FrequentItemSets sets = null;
        int retryCount = 0;
        if (shouldFindMinimumNumber) {
            getProgress().setTotal(maximalNumberOfRetries);
            getProgress().setCheckForStop(false);
        }
        logger.info("FPGrowth begin to determine frequent items sets.");

        while (sets == null || sets.size() < minimumNumberOfItemsets && retryCount < maximalNumberOfRetries) {
            int currentMinTotalSupport = (int) Math.ceil(currentSupport * exampleSet.size());

            // pre-computing data properties
            ExampleSet workingSet = preprocessExampleSet(exampleSet);

            // determining attributes and their positive indices
            Attribute[] attributes = new Attribute[workingSet.getAttributes().size()];
            double[] positiveIndices = new double[workingSet.getAttributes().size()];
            int i = 0;
            for (Attribute attribute : workingSet.getAttributes()) {
                attributes[i] = attribute;
                positiveIndices[i] = attribute.getMapping().getPositiveIndex();
                String positiveValueString = null;
                try {
                    positiveValueString = getParameterAsString(PARAMETER_POSITIVE_VALUE);
                } catch (UndefinedParameterError err) {
                }
                if (positiveValueString != null) {
                    if (!positiveValueString.equals("")) {
                        positiveIndices[i] = attribute.getMapping().mapString(positiveValueString);
                    }
                }
                i++;
            }
            logger.info("FPGrowth determine attributes and their positive indices done.");

            // map attributes to items
            Map<Attribute, Item> itemMapping = getAttributeMapping(workingSet);
            logger.info("FPGrowth map attributes to items done.");

            // computing frequency of 1-Item Sets
            getItemFrequency(workingSet, attributes, positiveIndices, itemMapping);
            logger.info("FPGrowth computing frequency of 1-Item Sets done.");

            // eliminating non frequent items
            removeNonFrequentItems(itemMapping, currentMinTotalSupport, workingSet);
            logger.info("FPGrowth eliminating non frequent items done.");

            // generating FP Tree
            FPTree tree = getFPTree(workingSet, attributes, positiveIndices, itemMapping);
            logger.info("FPGrowth generating FP Tree done.");

            // mine tree
            sets = new FrequentItemSets(workingSet.size());
            String mustContainItems = getParameterAsString(PARAMETER_MUST_CONTAIN);
            if (mustContainItems == null || mustContainItems.isEmpty()) {
                mineTree(tree, sets, 0, new FrequentItemSet(), currentMinTotalSupport, maxItems, !shouldFindMinimumNumber);
                logger.info("FPGrowth mine tree done with 'mustContainItems' not set.");
            } else {
                FrequentItemSet conditionalItems = new FrequentItemSet();
                Pattern pattern = Pattern.compile(mustContainItems);
                Map<Item, Header> headerTable = tree.getHeaderTable();
                int depth = 0;
                boolean supportOfMandatoryItemsTooLow = false;
                for (Entry<Attribute, Item> attributeEntry : itemMapping.entrySet()) {
                    Matcher matcher = pattern.matcher(attributeEntry.getKey().getName());
                    Item targetItem = attributeEntry.getValue();

                    Header targetItemHeader = headerTable.get(targetItem);
                    int itemFrequency = 0;
                    if (targetItemHeader != null) {
                        itemFrequency = targetItemHeader.getFrequencies().getFrequency(depth);
                    }

                    if (matcher.matches()) {
                        if (itemFrequency >= currentMinTotalSupport) {
                            // building conditional items
                            // run over sibling chain
                            for (FPTreeNode node : targetItemHeader.getSiblingChain()) {
                                // and propagate frequency to root
                                int frequency = node.getFrequency(depth);
                                // if frequency is positive
                                if (frequency > 0) {
                                    FPTreeNode currentNode = node.getFather();
                                    while (currentNode != tree) {
                                        // increase node frequency
                                        currentNode.increaseFrequency(depth + 1, frequency);
                                        // increase item frequency in
                                        // headerTable
                                        headerTable.get(currentNode.getNodeItem()).getFrequencies()
                                                .increaseFrequency(depth + 1, frequency);
                                        // go up in tree
                                        currentNode = currentNode.getFather();
                                    }

                                    // also descend into subpaths and increase
                                    // frequencies (see function comment)
                                    recursivelyIncreaseFrequencyOfNextDepth(headerTable, node, depth);
                                }
                            }
                            // add item to conditional items
                            int itemSupport = targetItemHeader.getFrequencies().getFrequency(depth);
                            conditionalItems.addItem(targetItem, itemSupport);

                            depth++;
                        } else {
                            // at least one of the mandatory items does not have sufficient support
                            // -> break and return empty itemset list.
                            supportOfMandatoryItemsTooLow = true;
                            break;
                        }
                    }
                }

                // don't do anything if the mandatory items have too low support
                if (!supportOfMandatoryItemsTooLow) {
                    if (!conditionalItems.getItems().isEmpty()) {
                        if (conditionalItems.getFrequency() >= currentMinTotalSupport) {
                            // add this conditional items to frequentSets
                            sets.addFrequentSet(conditionalItems);
                        } else {
                            supportOfMandatoryItemsTooLow = true;
                        }
                    }
                }

                // only mine tree if the mandatory attributes have sufficient support
                if (!supportOfMandatoryItemsTooLow) {
                    mineTree(tree, sets, depth, conditionalItems, currentMinTotalSupport, maxItems,
                            !shouldFindMinimumNumber);
                }

                logger.info("FPGrowth mine tree done with 'mustContainItems': %s.", mustContainItems);
            }

            currentSupport *= 0.9;
            retryCount++;

            // trigger progress
            if (shouldFindMinimumNumber) {
                getProgress().step();
            }
        }

        exampleSetOutput.deliver(exampleSet);
        frequentSetsOutput.deliver(sets);

        logger.info("FPGrowth done and deliver set successfully.");
    }

    /**
     * Recursivly increases the frequencies of the next recursion level for
     * <code>startingNode</code> and all its descendants.
     * <p>
     * This is necessary for the constrained search of frequent item sets, where only some branches
     * of the fptree are mined, e.g. when searching for itemsets which must contain a user-defined
     * set of items.
     * <p>
     * Usually, the subtrees are reached via the sibling link of the header items when mining the
     * other branches of the tree. When they are filtered away, this has to be done explicitly when
     * adding matching items.
     * <p>
     * For more information see: Mining Frequent Patterns without Candidate Generation: A
     * Frequent-Pattern Tree Approach by Han, Pei, Yin and Mao 2001.
     * <p>
     * For the note on constrained sets, see section 6.2.1: FP-tree mining with constraints.
     *
     * @param headerTable
     * @param startingNode
     * @param currentDepth
     */
    private void recursivelyIncreaseFrequencyOfNextDepth(Map<Item, Header> headerTable, FPTreeNode startingNode,
                                                         int currentDepth) {
        int frequency = startingNode.getFrequency(currentDepth);
        if (frequency > 0) {
            startingNode.increaseFrequency(currentDepth + 1, frequency);
            headerTable.get(startingNode.getNodeItem()).getFrequencies().increaseFrequency(currentDepth + 1, frequency);
            for (FPTreeNode childNode : startingNode.getChildren().values()) {
                recursivelyIncreaseFrequencyOfNextDepth(headerTable, childNode, currentDepth);
            }
        }
    }

    private ExampleSet preprocessExampleSet(ExampleSet exampleSet) {
        // precomputing data properties
        ExampleSet workingSet = (ExampleSet) exampleSet.clone();

        // remove unusuable attributes
        int oldAttributeCount = workingSet.getAttributes().size();
        removeNonBooleanAttributes(workingSet);
        int newAttributeCount = workingSet.getAttributes().size();
        if (oldAttributeCount != newAttributeCount) {
            int removeCount = oldAttributeCount - newAttributeCount;
            String message = null;
            if (removeCount == 1) {
                message = "Removed 1 non-binominal attribute, frequent item set mining is only supported for the positive values of binominal attributes.";
            } else {
                message = "Removed " + removeCount
                        + " non-binominal attributes, frequent item set mining is only supported for the positive values of binominal attributes.";
            }
            logger.warn(message);
        }

        return workingSet;
    }

    private void mineTree(FPTree tree, FrequentItemSets sets, int recursionDepth, FrequentItemSet conditionalItems,
                          int minTotalSupport, int maxItems) throws ProcessStoppedException {
        mineTree(tree, sets, recursionDepth, conditionalItems, minTotalSupport, maxItems, false);
    }

    private void mineTree(FPTree tree, FrequentItemSets sets, int recursionDepth, FrequentItemSet conditionalItems,
                          int minTotalSupport, int maxItems, boolean showProgress) throws ProcessStoppedException {
        if (!treeIsEmpty(tree, recursionDepth)) {
            if (maxItems > 0) {
                if (recursionDepth >= maxItems) {
                    return;
                }
            }
            // recursively mine tree
            Map<Item, Header> headerTable = tree.getHeaderTable();
            Iterator<Entry<Item, Header>> headerIterator = headerTable.entrySet().iterator();
            if (showProgress && recursionDepth == 0) {
                getProgress().setTotal(headerTable.entrySet().size());
            }
            while (headerIterator.hasNext()) {
                Entry<Item, Header> headerEntry = headerIterator.next();
                Item item = headerEntry.getKey();
                Header itemHeader = headerEntry.getValue();
                // check for minSupport
                int itemSupport = itemHeader.getFrequencies().getFrequency(recursionDepth);
                if (itemSupport >= minTotalSupport && !conditionalItems.getItems().contains(item)) {
                    // run over sibling chain
                    for (FPTreeNode node : itemHeader.getSiblingChain()) {
                        // and propagate frequency to root
                        int frequency = node.getFrequency(recursionDepth);
                        // if frequency is positive
                        if (frequency > 0) {
                            FPTreeNode currentNode = node.getFather();
                            while (currentNode != tree) {
                                // increase node frequency
                                currentNode.increaseFrequency(recursionDepth + 1, frequency);
                                // increase item frequency in headerTable
                                headerTable.get(currentNode.getNodeItem()).getFrequencies()
                                        .increaseFrequency(recursionDepth + 1, frequency);
                                // go up in tree
                                currentNode = currentNode.getFather();
                            }
                        }
                    }
                    FrequentItemSet recursivConditionalItems = (FrequentItemSet) conditionalItems.clone();
                    // add item to conditional items
                    recursivConditionalItems.addItem(item, itemSupport);
                    // add this conditional items to frequentSets
                    sets.addFrequentSet(recursivConditionalItems);
                    // recursively mine new tree
                    mineTree(tree, sets, recursionDepth + 1, recursivConditionalItems, minTotalSupport, maxItems);
                    // run over sibling chain for popping frequency stack
                    for (FPTreeNode node : itemHeader.getSiblingChain()) {
                        // and remove propagation of frequency
                        FPTreeNode currentNode = node.getFather();
                        while (currentNode != tree) {
                            // pop frequency
                            currentNode.popFrequency(recursionDepth + 1);
                            // go up in tree
                            currentNode = currentNode.getFather();
                        }
                    }
                    // pop frequencies of every header table on current
                    // recursion depth
                    for (Header currentItemHeader : headerTable.values()) {
                        currentItemHeader.getFrequencies().popFrequency(recursionDepth + 1);
                    }
                }
                if (recursionDepth == 0 && showProgress) {
                    getProgress().step();
                }
            }
            if (!showProgress) {
                checkForStop();
            }
        }
    }

    /**
     * Removes every non boolean attribute.
     *
     * @param exampleSet exampleSet, which attributes are tested
     */
    private void removeNonBooleanAttributes(ExampleSet exampleSet) {
        // removing non boolean attributes
        Collection<Attribute> deleteAttributes = new ArrayList<Attribute>();
        for (Attribute attribute : exampleSet.getAttributes()) {
            if (!attribute.isNominal() || attribute.getMapping().size() != 2) {
                deleteAttributes.add(attribute);
            }
        }
        for (Attribute attribute : deleteAttributes) {
            exampleSet.getAttributes().remove(attribute);
        }
    }

    /**
     * This method maps the attributes of the given exampleSet to an Item.
     *
     * @param exampleSet the exampleSet which attributes are mapped
     */
    private Map<Attribute, Item> getAttributeMapping(ExampleSet exampleSet) {
        // computing Attributes to test, because only boolean attributes are
        // used
        Map<Attribute, Item> mapping = new HashMap<Attribute, Item>();
        for (Attribute attribute : exampleSet.getAttributes()) {
            mapping.put(attribute, new BooleanAttributeItem(attribute));
        }
        return mapping;
    }

    /**
     * This method scans the exampleSet and counts the frequency of every item
     *
     * @param exampleSet the exampleSet to be scaned
     * @param mapping    the mapping of attributes to items
     */
    private void getItemFrequency(ExampleSet exampleSet, Attribute[] attributes, double[] positiveIndices,
                                  Map<Attribute, Item> mapping) {
        // iterate over exampleSet, counting item frequency
        for (Example currentExample : exampleSet) {
            int i = 0;
            for (Attribute attribute : attributes) {
                // if attribute is boolean and if attribute is the positive one
                // --> increase frequency of item
                if (currentExample.getValue(attribute) == positiveIndices[i]) {
                    mapping.get(attribute).increaseFrequency();
                }
                i++;
            }
        }
    }

    private void removeNonFrequentItems(Map<Attribute, Item> mapping, int minFrequency, ExampleSet exampleSet) {
        Collection<Attribute> deleteMappings = new ArrayList<Attribute>();
        Iterator<Entry<Attribute, Item>> it = mapping.entrySet().iterator();
        while (it.hasNext()) {
            Entry<Attribute, Item> entry = it.next();
            if (entry.getValue().getFrequency() < minFrequency) {
                deleteMappings.add(entry.getKey());
            }
        }
        for (Attribute attribute : deleteMappings) {
            exampleSet.getAttributes().remove(attribute);
        }
    }

    /**
     * Returns a new FPTree, representing the complete ExampleSet.
     *
     * @param exampleSet is the exampleSet, which shall be represented
     * @param mapping    is the mapping of attributes of the exampleSet to items
     */
    private FPTree getFPTree(ExampleSet exampleSet, Attribute[] attributes, double[] positiveIndices,
                             Map<Attribute, Item> mapping) {
        FPTree tree = new FPTree();
        for (Example currentExample : exampleSet) {
            List<Item> itemSet = new ArrayList<Item>();
            int i = 0;
            for (Attribute currentAttribute : attributes) {
                if (currentExample.getValue(currentAttribute) == positiveIndices[i]) {
                    itemSet.add(mapping.get(currentAttribute));
                }
                i++;
            }
            Collections.sort(itemSet);
            tree.addItemSet(itemSet, 1);
        }
        return tree;
    }

    private boolean treeIsEmpty(FPTree tree, int recursionDepth) {
        // tree is empty if every child of rootnode has frequency of 0 on top of
        // stack
        for (FPTreeNode node : tree.getChildren().values()) {
            if (node.getFrequency(recursionDepth) > 0) {
                return false;
            }
        }
        return true;
    }

    @Override
    public IOContainer getResult() {
        List<IOObject> ioObjects = new ArrayList<>();
        ioObjects.add(exampleSetOutput.getAnyDataOrNull());
        ioObjects.add(frequentSetsOutput.getAnyDataOrNull());
        return new IOContainer(ioObjects);
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = new ParameterTypeBoolean(PARAMETER_FIND_MIN_NUMBER_OF_ITEMSETS,
                I18N.getMessage("pio.FPGrowth.find_min_number_of_itemsets"),
                true);
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeInt(PARAMETER_MIN_NUMBER_OF_ITEMSETS,
                I18N.getMessage("pio.FPGrowth.min_number_of_itemsets"),
                0, Integer.MAX_VALUE, 100);
        type.registerDependencyCondition(
                new BooleanParameterCondition(this, PARAMETER_FIND_MIN_NUMBER_OF_ITEMSETS, true, true));
        type.setExpert(false);
        types.add(type);
        type = new ParameterTypeInt(PARAMETER_MAX_REDUCTION_STEPS,
                I18N.getMessage("pio.FPGrowth.max_number_of_retries"),
                2, Integer.MAX_VALUE, 15);
        type.registerDependencyCondition(
                new BooleanParameterCondition(this, PARAMETER_FIND_MIN_NUMBER_OF_ITEMSETS, false, true));
        type.setExpert(true);
        types.add(type);

        type = new ParameterTypeString(PARAMETER_POSITIVE_VALUE,
                I18N.getMessage("pio.FPGrowth.positive_value"),
                true);
        type.setExpert(true);
        types.add(type);
        types.add(new ParameterTypeDouble(PARAMETER_MIN_SUPPORT,
                I18N.getMessage("pio.FPGrowth.min_support"), 0.0d, 1.0d, 0.95d));
        types.add(new ParameterTypeInt(PARAMETER_MAX_ITEMS,
                I18N.getMessage("pio.FPGrowth.max_items"), -1, Integer.MAX_VALUE, -1));
        types.add(new ParameterTypeString(PARAMETER_MUST_CONTAIN,
                I18N.getMessage("pio.FPGrowth.must_contain")));

        /*type = new ParameterTypeBoolean(PARAMETER_KEEP_EXAMPLE_SET, "indicates if example set is kept", false);
        type.setDeprecated();
        types.add(type);*/
        return types;
    }
}
