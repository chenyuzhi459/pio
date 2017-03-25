package io.sugo.pio.operator.learner.tree;


import com.metamx.common.logger.Logger;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Statistics;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.Model;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.learner.AbstractLearner;
import io.sugo.pio.operator.learner.PredictionModel;
import io.sugo.pio.operator.learner.tree.criterions.*;
import io.sugo.pio.parameter.*;
import io.sugo.pio.parameter.conditions.BooleanParameterCondition;

import java.util.List;

/**
 * This is the abstract super class for all decision tree learners that can learn in parallel. The
 * actual type of the tree is determined by the criterion, e.g. using gain_ratio or Gini for CART /
 * C4.5 and chi_squared for CHAID.
 */
public abstract class AbstractParallelTreeLearner extends AbstractLearner {

    private static final Logger logger = new Logger(AbstractParallelTreeLearner.class);

    /**
     * The parameter name for &quot;Specifies the used criterion for selecting attributes and
     * numerical splits.&quot;
     */
    public static final String PARAMETER_CRITERION = "criterion";

    /**
     * The parameter name for &quot;The minimal size of all leaves.&quot;
     */
    public static final String PARAMETER_MINIMAL_SIZE_FOR_SPLIT = "minimal_size_for_split";

    /**
     * The parameter name for &quot;The minimal size of all leaves.&quot;
     */
    public static final String PARAMETER_MINIMAL_LEAF_SIZE = "minimal_leaf_size";

    /**
     * The parameter name for the minimal gain.
     */
    public static final String PARAMETER_MINIMAL_GAIN = "minimal_gain";

    /**
     * The parameter name for the maximum tree depth.
     */
    public static final String PARAMETER_MAXIMAL_DEPTH = "maximal_depth";

    /**
     * The parameter name for &quot;The confidence level used for pruning.&quot;
     */
    public static final String PARAMETER_CONFIDENCE = "confidence";

    /**
     * The parameter name for &quot;Enables the pruning and delivers a pruned tree.&quot;
     */
    public static final String PARAMETER_PRUNING = "apply_pruning";

    public static final String PARAMETER_PRE_PRUNING = "apply_prepruning";

    public static final String PARAMETER_NUMBER_OF_PREPRUNING_ALTERNATIVES = "number_of_prepruning_alternatives";

    public static final String[] CRITERIA_NAMES = {"gain_ratio", "information_gain", "gini_index", "accuracy"};

    public static final String[] CRITERIA_NAMES_DESC = {
            I18N.getMessage("pio.AbstractParallelTreeLearner.criteria_names_desc.gain_ratio"),
            I18N.getMessage("pio.AbstractParallelTreeLearner.criteria_names_desc.information_gain"),
            I18N.getMessage("pio.AbstractParallelTreeLearner.criteria_names_desc.gini_index"),
            I18N.getMessage("pio.AbstractParallelTreeLearner.criteria_names_desc.accuracy")
    };

    public static final Class<?>[] CRITERIA_CLASSES = {GainRatioColumnCriterion.class, InfoGainColumnCriterion.class,
            GiniIndexColumnCriterion.class, AccuracyColumnCriterion.class};

    public static final int CRITERION_GAIN_RATIO = 0;

    public static final int CRITERION_INFO_GAIN = 1;

    public static final int CRITERION_GINI_INDEX = 2;

    public static final int CRITERION_ACCURACY = 3;

    @Override
    public Class<? extends PredictionModel> getModelClass() {
        return TreeModel.class;
    }

    /**
     * Returns all termination criteria.
     */
    public abstract List<ColumnTerminator> getTerminationCriteria(ExampleSet exampleSet) throws OperatorException;

    /**
     * Returns the pruner for this tree learner. If this method returns null, pruning will be
     * disabled.
     */
    public abstract Pruner getPruner() throws OperatorException;

    /**
     * The split preprocessing is applied before each new split. If this method returns
     * <code>null</code> as in the default implementation the preprocessing step is skipped.
     * Subclasses might want to override this in order to perform some data preprocessing like
     * random subset selections. The default implementation of this method always returns
     * <code>null</code> independent of the seed.
     *
     * @param seed the seed for the { RandomGenerator} used for random subset selection. Not
     *             used in the default implementation.
     * @return
     */
    public AttributePreprocessing getSplitPreprocessing(int seed) {
        return null;
    }

    @Override
    public Model learn(ExampleSet eSet) throws OperatorException {
        logger.info("Parallel tree learner begin to learn through example set[%s]...", eSet.getName());

        ExampleSet exampleSet = (ExampleSet) eSet.clone();

        // check if the label attribute contains any missing values
        Attribute labelAtt = exampleSet.getAttributes().getLabel();
        exampleSet.recalculateAttributeStatistics(labelAtt);
        if (exampleSet.getStatistics(labelAtt, Statistics.UNKNOWN) > 0) {
            throw new UserError(this, "pio.error.operator.label_miss_values", labelAtt.getName());
        }

        // create tree builder
        AbstractParallelTreeBuilder builder = getTreeBuilder(exampleSet);
        // learn tree
        Tree root = builder.learnTree(exampleSet);

        logger.info("Parallel tree learner learn through example set[%s] finished, and return the model.", eSet.getName());

        // create and return model
        return new TreeModel(exampleSet, root);
    }

    protected abstract AbstractParallelTreeBuilder getTreeBuilder(ExampleSet exampleSet) throws OperatorException;

    protected ColumnCriterion createCriterion() throws OperatorException {
        if (getParameterAsBoolean(PARAMETER_PRE_PRUNING)) {
            return AbstractColumnCriterion.createColumnCriterion(this, getParameterAsDouble(PARAMETER_MINIMAL_GAIN));
        } else {
            return AbstractColumnCriterion.createColumnCriterion(this, 0);
        }
    }

    @Override
    public List<ParameterType> getParameterTypes() {
        List<ParameterType> types = super.getParameterTypes();
        ParameterType type = new ParameterTypeStringCategory(PARAMETER_CRITERION,
                I18N.getMessage("pio.ParallelDecisionTreeLearner.criterion"),
//				"Specifies the used criterion for selecting attributes and numerical splits.",
                CRITERIA_NAMES, CRITERIA_NAMES_DESC,
                CRITERIA_NAMES[CRITERION_GAIN_RATIO], false);
        type.setOptional(false);
        types.add(type);

        type = new ParameterTypeInt(PARAMETER_MAXIMAL_DEPTH,
                I18N.getMessage("pio.ParallelDecisionTreeLearner.maximal_depth"),
//				"The maximum tree depth (-1: no bound)",
                -1, Integer.MAX_VALUE,
                20);
        type.setOptional(false);
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_PRUNING,
                I18N.getMessage("pio.ParallelDecisionTreeLearner.apply_pruning"),
//				"Activates the pruning of the tree.",
                true);
        types.add(type);

        type = new ParameterTypeDouble(PARAMETER_CONFIDENCE,
                I18N.getMessage("pio.ParallelDecisionTreeLearner.confidence"),
//				"The confidence level used for the pessimistic error calculation of pruning.",
                0.0000001, 0.5, 0.25);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_PRUNING, false, true));
        types.add(type);

        type = new ParameterTypeBoolean(PARAMETER_PRE_PRUNING,
                I18N.getMessage("pio.ParallelDecisionTreeLearner.apply_prepruning"),
//				"Activates the pre pruning and delivers a prepruned tree.",
                true);
        types.add(type);

        type = new ParameterTypeDouble(PARAMETER_MINIMAL_GAIN,
                I18N.getMessage("pio.ParallelDecisionTreeLearner.minimal_gain"),
//				"The minimal gain which must be achieved in order to produce a split.",
                0.0d, Double.POSITIVE_INFINITY, 0.1d);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_PRE_PRUNING, false, true));
        types.add(type);
        type = new ParameterTypeInt(PARAMETER_MINIMAL_LEAF_SIZE,
                I18N.getMessage("pio.ParallelDecisionTreeLearner.minimal_leaf_size"),
//				"The minimal size of all leaves.",
                1, Integer.MAX_VALUE, 2);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_PRE_PRUNING, false, true));
        types.add(type);

        type = new ParameterTypeInt(PARAMETER_MINIMAL_SIZE_FOR_SPLIT,
                I18N.getMessage("pio.ParallelDecisionTreeLearner.minimal_size_for_split"),
//				"The minimal size of a node in order to allow a split.",
                1, Integer.MAX_VALUE, 4);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_PRE_PRUNING, false, true));
        types.add(type);

        type = new ParameterTypeInt(PARAMETER_NUMBER_OF_PREPRUNING_ALTERNATIVES,
                I18N.getMessage("pio.ParallelDecisionTreeLearner.number_of_prepruning_alternatives"),
//				"The number of alternative nodes tried when prepruning would prevent a split.",
                0, Integer.MAX_VALUE, 3);
        type.registerDependencyCondition(new BooleanParameterCondition(this, PARAMETER_PRE_PRUNING, false, true));
        types.add(type);

        return types;
    }
}
