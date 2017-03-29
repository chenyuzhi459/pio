package io.sugo.pio.operator.learner.associations;

import com.metamx.common.logger.Logger;
import io.sugo.pio.constant.PortConstant;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.*;
import io.sugo.pio.operator.learner.associations.fpgrowth.FPGrowth;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeCategory;
import io.sugo.pio.parameter.ParameterTypeDouble;
import io.sugo.pio.parameter.conditions.EqualTypeCondition;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.metadata.GenerateNewMDRule;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;


/**
 * <p>
 * This operator generates association rules from frequent item sets. In RapidMiner, the process of
 * frequent item set mining is divided into two parts: first, the generation of frequent item sets
 * and second, the generation of association rules from these sets.
 * </p>
 * <p>
 * <p>
 * For the generation of frequent item sets, you can use for example the operator {@link FPGrowth}.
 * The result will be a set of frequent item sets which could be used as input for this operator.
 * </p>
 */
public class AssociationRuleGenerator extends Operator {

    private static final Logger logger = new Logger(AssociationRuleGenerator.class);

    private InputPort itemSetsInput = getInputPorts().createPort(PortConstant.ITEM_SETS, PortConstant.ITEM_SETS_DESC,FrequentItemSets.class);
    private OutputPort rulesOutput = getOutputPorts().createPort(PortConstant.RULES, PortConstant.RULES_DESC);
    private OutputPort itemSetsOutput = getOutputPorts().createPort(PortConstant.ITEM_SETS, PortConstant.ITEM_SETS_DESC);

    public static final String PARAMETER_CRITERION = "criterion";

    public static final String PARAMETER_MIN_CONFIDENCE = "min_confidence";

    public static final String PARAMETER_MIN_CRITERION_VALUE = "min_criterion_value";

    public static final String PARAMETER_GAIN_THETA = "gain_theta";

    public static final String PARAMETER_LAPLACE_K = "laplace_k";

    /*public static final String[] CRITERIA = {"confidence", "lift", "conviction", "ps", "gain", "laplace"};*/
    public static final String[] CRITERIA = {
            I18N.getMessage("pio.AssociationRuleGenerator.criteria.confidence"),
            I18N.getMessage("pio.AssociationRuleGenerator.criteria.lift"),
            I18N.getMessage("pio.AssociationRuleGenerator.criteria.conviction"),
            I18N.getMessage("pio.AssociationRuleGenerator.criteria.ps"),
            I18N.getMessage("pio.AssociationRuleGenerator.criteria.gain"),
            I18N.getMessage("pio.AssociationRuleGenerator.criteria.laplace")
    };

    public static final int CONFIDENCE = 0;
    public static final int LIFT = 1;
    public static final int CONVICTION = 2;
    public static final int PS = 3;
    public static final int GAIN = 4;
    public static final int LAPLACE = 5;

    public AssociationRuleGenerator() {
        super();
        getTransformer().addRule(new GenerateNewMDRule(rulesOutput, AssociationRules.class));
        getTransformer().addPassThroughRule(itemSetsInput, itemSetsOutput);
    }

    @Override
    public String getDefaultFullName() {
        return I18N.getMessage("pio.AssociationRuleGenerator.name");
    }

    @Override
    public OperatorGroup getGroup() {
        return OperatorGroup.association;
    }

    @Override
    public String getDescription() {
        return I18N.getMessage("pio.AssociationRuleGenerator.description");
    }

    @Override
    public int getSequence() {
        return 0;
    }

    @Override
    public void doWork() throws OperatorException {
        double minValue = getParameterAsDouble(PARAMETER_MIN_CONFIDENCE);
        if (getParameterAsInt(PARAMETER_CRITERION) != CONFIDENCE) {
            minValue = getParameterAsDouble(PARAMETER_MIN_CRITERION_VALUE);
        }
        double theta = getParameterAsDouble(PARAMETER_GAIN_THETA);
        double laplaceK = getParameterAsDouble(PARAMETER_LAPLACE_K);
        FrequentItemSets sets = itemSetsInput.getData(FrequentItemSets.class);
        logger.info("AssociationRuleGenerator begin to create association rules, frequent item sets size[%d].", sets.size());

        AssociationRules rules = new AssociationRules();
        HashMap<Collection<Item>, Integer> setFrequencyMap = new HashMap<Collection<Item>, Integer>();
        int numberOfTransactions = sets.getNumberOfTransactions();

        // iterating sorted over every frequent Set, generating every possible rule and building
        // frequency map
        sets.sortSets();
        int progressCounter = 0;
        getProgress().setTotal(sets.size());
        logger.info("AssociationRuleGenerator iterating sorted over every frequent set, generating every possible rule and building frequency map.");

        for (FrequentItemSet set : sets) {
            setFrequencyMap.put(set.getItems(), set.getFrequency());
            // generating rule by splitting set in every two parts for head and body of rule
            if (set.getItems().size() > 1) {
                PowerSet<Item> powerSet = new PowerSet<Item>(set.getItems());
                for (Collection<Item> premises : powerSet) {
                    if (premises.size() > 0 && premises.size() < set.getItems().size()) {
                        Collection<Item> conclusion = powerSet.getComplement(premises);
                        int totalFrequency = set.getFrequency();
                        int preconditionFrequency = setFrequencyMap.get(premises);
                        int conclusionFrequency = setFrequencyMap.get(conclusion);

                        double value = getCriterionValue(totalFrequency, preconditionFrequency, conclusionFrequency,
                                numberOfTransactions, theta, laplaceK);
                        if (value >= minValue) {
                            AssociationRule rule = new AssociationRule(premises, conclusion,
                                    getSupport(totalFrequency, numberOfTransactions));
                            rule.setConfidence(getConfidence(totalFrequency, preconditionFrequency));
                            rule.setLift(getLift(totalFrequency, preconditionFrequency, conclusionFrequency,
                                    numberOfTransactions));
                            rule.setConviction(getConviction(totalFrequency, preconditionFrequency, conclusionFrequency,
                                    numberOfTransactions));
                            rule.setPs(
                                    getPs(totalFrequency, preconditionFrequency, conclusionFrequency, numberOfTransactions));
                            rule.setGain(getGain(theta, totalFrequency, preconditionFrequency, conclusionFrequency,
                                    numberOfTransactions));
                            rule.setLaplace(getLaPlace(laplaceK, totalFrequency, preconditionFrequency, conclusionFrequency,
                                    numberOfTransactions));
                            rules.addItemRule(rule);
                        }
                    }
                }
            }
            if (++progressCounter % 100 == 0) {
                getProgress().step(100);
            }
        }
        rules.sort();
        rulesOutput.deliver(rules);
        itemSetsOutput.deliver(sets);

        logger.info("AssociationRuleGenerator create association rules and deliver sets successfully.");
    }

    private double getCriterionValue(int totalFrequency, int preconditionFrequency, int conclusionFrequency,
                                     int numberOfTransactions, double theta, double laplaceK) throws OperatorException {
        int criterion = getParameterAsInt(PARAMETER_CRITERION);
        switch (criterion) {
            case LIFT:
                return getLift(totalFrequency, preconditionFrequency, conclusionFrequency, numberOfTransactions);
            case CONVICTION:
                return getConviction(totalFrequency, preconditionFrequency, conclusionFrequency, numberOfTransactions);
            case PS:
                return getPs(totalFrequency, preconditionFrequency, conclusionFrequency, numberOfTransactions);
            case GAIN:
                return getGain(theta, totalFrequency, preconditionFrequency, conclusionFrequency, numberOfTransactions);
            case LAPLACE:
                return getLaPlace(laplaceK, totalFrequency, preconditionFrequency, conclusionFrequency,
                        numberOfTransactions);
            case CONFIDENCE:
            default:
                return getConfidence(totalFrequency, preconditionFrequency);
        }
    }

    private double getGain(double theta, int totalFrequency, int preconditionFrequency, int conclusionFrequency,
                           int numberOfTransactions) {
        return getSupport(totalFrequency, numberOfTransactions)
                - theta * getSupport(preconditionFrequency, numberOfTransactions);
    }

    private double getLift(int totalFrequency, int preconditionFrequency, int conclusionFrequency,
                           int numberOfTransactions) {
        return (double) totalFrequency * (double) numberOfTransactions
                / ((double) preconditionFrequency * conclusionFrequency);
    }

    private double getPs(int totalFrequency, int preconditionFrequency, int conclusionFrequency, int numberOfTransactions) {
        return getSupport(totalFrequency, numberOfTransactions) - getSupport(preconditionFrequency, numberOfTransactions)
                * getSupport(conclusionFrequency, numberOfTransactions);
    }

    private double getLaPlace(double k, int totalFrequency, int preconditionFrequency, int conclusionFrequency,
                              int numberOfTransactions) {
        return (getSupport(totalFrequency, numberOfTransactions) + 1d)
                / (getSupport(preconditionFrequency, numberOfTransactions) + k);
    }

    private double getConviction(int totalFrequency, int preconditionFrequency, int conclusionFrequency,
                                 int numberOfTransactions) {
        double numerator = preconditionFrequency * (numberOfTransactions - conclusionFrequency);
        double denumerator = numberOfTransactions * (preconditionFrequency - totalFrequency);
        return numerator / denumerator;
    }

    private double getConfidence(int totalFrequency, int preconditionFrequency) {
        return (double) totalFrequency / (double) preconditionFrequency;
    }

    private double getSupport(int frequency, int completeSize) {
        return (double) frequency / (double) completeSize;
    }

    @Override
    public IOContainer getResult() {
        List<IOObject> ioObjects = new ArrayList<>();
        ioObjects.add(rulesOutput.getAnyDataOrNull());
        ioObjects.add(itemSetsOutput.getAnyDataOrNull());
        return new IOContainer(ioObjects);
    }

    @Override
    public boolean shouldAutoConnect(OutputPort port) {
        if (port == itemSetsOutput) {
            return getParameterAsBoolean("keep_frequent_item_sets");
        } else {
            return super.shouldAutoConnect(port);
        }
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();

        ParameterType type = new ParameterTypeCategory(PARAMETER_CRITERION,
                I18N.getMessage("pio.AssociationRuleGenerator.criterion"), CRITERIA, 0);
        type.setExpert(false);
        types.add(type);

        type = new ParameterTypeDouble(PARAMETER_MIN_CONFIDENCE, I18N.getMessage("pio.AssociationRuleGenerator.min_confidence"),
                0.0d, 1.0d, 0.8d);
        type.setExpert(false);
        type.registerDependencyCondition(new EqualTypeCondition(this, PARAMETER_CRITERION, CRITERIA, true, CONFIDENCE));
        types.add(type);

        type = new ParameterTypeDouble(PARAMETER_MIN_CRITERION_VALUE,
                I18N.getMessage("pio.AssociationRuleGenerator.min_criterion_value"), Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY, 0.8d);
        type.setExpert(false);
        type.registerDependencyCondition(
                new EqualTypeCondition(this, PARAMETER_CRITERION, CRITERIA, true, LIFT, CONVICTION, PS, GAIN, LAPLACE));
        types.add(type);

        type = new ParameterTypeDouble(PARAMETER_GAIN_THETA, I18N.getMessage("pio.AssociationRuleGenerator.gain_theta"),
                Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, 2d);
        type.setExpert(true);
        types.add(type);

        type = new ParameterTypeDouble(PARAMETER_LAPLACE_K, I18N.getMessage("pio.AssociationRuleGenerator.laplace_k"), 1,
                Double.POSITIVE_INFINITY, 1d);
        type.setExpert(true);
        types.add(type);

        return types;
    }
}
