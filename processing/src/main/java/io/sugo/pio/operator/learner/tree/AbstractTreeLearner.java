package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Statistics;
import io.sugo.pio.operator.Model;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.UserError;
import io.sugo.pio.operator.learner.AbstractLearner;
import io.sugo.pio.operator.learner.PredictionModel;
import io.sugo.pio.operator.learner.tree.criterions.*;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeDouble;
import io.sugo.pio.parameter.ParameterTypeInt;
import io.sugo.pio.parameter.ParameterTypeStringCategory;

import java.util.List;

/**
 * This is the abstract super class for all decision tree learners. The actual type of the tree is
 * determined by the criterion, e.g. using gain_ratio or Gini for CART / C4.5 and chi_squared for
 * CHAID.
 *
 * @author Sebastian Land, Ingo Mierswa
 */
public abstract class AbstractTreeLearner extends AbstractLearner {

	/**
	 * The parameter name for &quot;Specifies the used criterion for selecting attributes and
	 * numerical splits.&quot;
	 */
	public static final String PARAMETER_CRITERION = "criterion";

	/** The parameter name for &quot;The minimal size of all leaves.&quot; */
	public static final String PARAMETER_MINIMAL_SIZE_FOR_SPLIT = "minimal_size_for_split";

	/** The parameter name for &quot;The minimal size of all leaves.&quot; */
	public static final String PARAMETER_MINIMAL_LEAF_SIZE = "minimal_leaf_size";

	/** The parameter name for the minimal gain. */
	public static final String PARAMETER_MINIMAL_GAIN = "minimal_gain";

	public static final String[] CRITERIA_NAMES = { "gain_ratio", "information_gain", "gini_index", "accuracy" };

	public static final Class<?>[] CRITERIA_CLASSES = { GainRatioCriterion.class, InfoGainCriterion.class,
		GiniIndexCriterion.class, AccuracyCriterion.class };

	public static final int CRITERION_GAIN_RATIO = 0;

	public static final int CRITERION_INFO_GAIN = 1;

	public static final int CRITERION_GINI_INDEX = 2;

	public static final int CRITERION_ACCURACY = 3;

	@Override
	public Class<? extends PredictionModel> getModelClass() {
		return TreeModel.class;
	}

	/** Returns all termination criteria. */
	public abstract List<Terminator> getTerminationCriteria(ExampleSet exampleSet) throws OperatorException;

	/**
	 * Returns the pruner for this tree learner. If this method returns null, pruning will be
	 * disabled.
	 */
	public abstract Pruner getPruner() throws OperatorException;

	/**
	 * The split preprocessing is applied before each new split The default implementation does
	 * nothing and simply returns the given example set. Subclasses might want to override this in
	 * order to perform some data preprocessing like random subset selections.
	 */
	public SplitPreprocessing getSplitPreprocessing() {
		return null;
	}

	@Override
	public Model learn(ExampleSet eSet) throws OperatorException {
		ExampleSet exampleSet = (ExampleSet) eSet.clone();

		// check if the label attribute contains any missing values
		Attribute labelAtt = exampleSet.getAttributes().getLabel();
		exampleSet.recalculateAttributeStatistics(labelAtt);
		if (exampleSet.getStatistics(labelAtt, Statistics.UNKNOWN) > 0) {
			throw new UserError(this, "pio.error.operator.label_miss_values", labelAtt.getName());
		}

		// create tree builder
		TreeBuilder builder = getTreeBuilder(exampleSet);

		// learn tree
		Tree root = builder.learnTree(exampleSet);

		// create and return model
		return new TreeModel(exampleSet, root);
	}

	protected abstract TreeBuilder getTreeBuilder(ExampleSet exampleSet) throws OperatorException;

	protected Criterion createCriterion(double minimalGain) throws OperatorException {
		return AbstractCriterion.createCriterion(this, minimalGain);
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		ParameterType type = new ParameterTypeStringCategory(PARAMETER_CRITERION,
				"Specifies the used criterion for selecting attributes and numerical splits.", CRITERIA_NAMES,
				CRITERIA_NAMES[CRITERION_GAIN_RATIO], false);
		types.add(type);
		type = new ParameterTypeInt(PARAMETER_MINIMAL_SIZE_FOR_SPLIT,
				"The minimal size of a node in order to allow a split.", 1, Integer.MAX_VALUE, 4);
		types.add(type);
		type = new ParameterTypeInt(PARAMETER_MINIMAL_LEAF_SIZE, "The minimal size of all leaves.", 1, Integer.MAX_VALUE, 2);
		types.add(type);
		types.add(new ParameterTypeDouble(PARAMETER_MINIMAL_GAIN,
				"The minimal gain which must be achieved in order to produce a split.", 0.0d, Double.POSITIVE_INFINITY, 0.1d));
		return types;
	}
}
