package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorCapability;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.parameter.ParameterTypeBoolean;
import io.sugo.pio.parameter.ParameterTypeDouble;
import io.sugo.pio.parameter.ParameterTypeInt;

import java.util.LinkedList;
import java.util.List;

/**
 * <p>
 * This operator learns decision trees from both nominal and numerical data. Decision trees are
 * powerful classification methods which often can also easily be understood. This decision tree
 * learner works similar to Quinlan's C4.5 or CART.
 * </p>
 *
 * <p>
 * The actual type of the tree is determined by the criterion, e.g. using gain_ratio or Gini for
 * CART / C4.5.
 * </p>
 *
 *
 * @author Sebastian Land, Ingo Mierswa
 */
public class DecisionTreeLearner extends AbstractTreeLearner {

	/** The parameter name for the maximum tree depth. */
	public static final String PARAMETER_MAXIMAL_DEPTH = "maximal_depth";

	/** The parameter name for &quot;The confidence level used for pruning.&quot; */
	public static final String PARAMETER_CONFIDENCE = "confidence";

	/** The parameter name for &quot;Disables the pruning and delivers an unpruned tree.&quot; */
	public static final String PARAMETER_NO_PRUNING = "no_pruning";

	public static final String PARAMETER_NO_PRE_PRUNING = "no_pre_pruning";

	public static final String PARAMETER_NUMBER_OF_PREPRUNING_ALTERNATIVES = "number_of_prepruning_alternatives";

	@Override
	public String getDefaultFullName() {
		return I18N.getMessage("pio.DecisionTreeLearner.name");
	}

	@Override
	public String getDescription() {
		return I18N.getMessage("pio.DecisionTreeLearner.description");
	}

	@Override
	public OperatorGroup getGroup() {
		return OperatorGroup.classification;
	}

	@Override
	public Pruner getPruner() throws OperatorException {
		if (!getParameterAsBoolean(PARAMETER_NO_PRUNING)) {
			return new PessimisticPruner(getParameterAsDouble(PARAMETER_CONFIDENCE), new DecisionTreeLeafCreator());
		} else {
			return null;
		}
	}

	@Override
	public List<Terminator> getTerminationCriteria(ExampleSet exampleSet) throws OperatorException {
		List<Terminator> result = new LinkedList<>();
		result.add(new SingleLabelTermination());
		result.add(new NoAttributeLeftTermination());
		result.add(new EmptyTermination());
		int maxDepth = getParameterAsInt(PARAMETER_MAXIMAL_DEPTH);
		if (maxDepth <= 0) {
			maxDepth = exampleSet.size();
		}
		result.add(new MaxDepthTermination(maxDepth));
		return result;
	}

	@Override
	public boolean supportsCapability(OperatorCapability capability) {
		switch (capability) {
			case BINOMINAL_ATTRIBUTES:
			case POLYNOMINAL_ATTRIBUTES:
			case NUMERICAL_ATTRIBUTES:
			case POLYNOMINAL_LABEL:
			case BINOMINAL_LABEL:
			case WEIGHTED_EXAMPLES:
			case MISSING_VALUES:
				return true;
			default:
				return false;
		}
	}

	@Override
	protected TreeBuilder getTreeBuilder(ExampleSet exampleSet) throws OperatorException {
		return new TreeBuilder(createCriterion(getParameterAsDouble(PARAMETER_MINIMAL_GAIN)),
				getTerminationCriteria(exampleSet), getPruner(), getSplitPreprocessing(), new DecisionTreeLeafCreator(),
				getParameterAsBoolean(PARAMETER_NO_PRE_PRUNING),
				getParameterAsInt(PARAMETER_NUMBER_OF_PREPRUNING_ALTERNATIVES),
				getParameterAsInt(PARAMETER_MINIMAL_SIZE_FOR_SPLIT), getParameterAsInt(PARAMETER_MINIMAL_LEAF_SIZE));
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();
		ParameterType type = new ParameterTypeInt(PARAMETER_MAXIMAL_DEPTH,
				"The maximum tree depth (-1: no bound)", -1,
				Integer.MAX_VALUE, 20);
		types.add(type);
		type = new ParameterTypeDouble(PARAMETER_CONFIDENCE,
				"The confidence level used for the pessimistic error calculation of pruning.", 0.0000001, 0.5, 0.25);
		types.add(type);

		types.add(new ParameterTypeInt(PARAMETER_NUMBER_OF_PREPRUNING_ALTERNATIVES,
				"The number of alternative nodes tried when prepruning would prevent a split.", 0, Integer.MAX_VALUE, 3));
		types.add(new ParameterTypeBoolean(PARAMETER_NO_PRE_PRUNING,
				"Disables the pre pruning and delivers a tree without any prepruning.", false));
		types.add(new ParameterTypeBoolean(PARAMETER_NO_PRUNING, "Disables the pruning and delivers an unpruned tree.",
				false));

		return types;
	}
}
