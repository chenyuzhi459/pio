package io.sugo.pio.operator.learner.tree;


import com.metamx.common.logger.Logger;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.i18n.I18N;
import io.sugo.pio.operator.OperatorCapability;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;
import io.sugo.pio.parameter.ParameterType;
import io.sugo.pio.studio.internal.Resources;

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
 */
public class ParallelDecisionTreeLearner extends AbstractParallelTreeLearner {

	private static final Logger logger = new Logger(ParallelDecisionTreeLearner.class);

	@Override
	public Pruner getPruner() throws OperatorException {
		if (getParameterAsBoolean(PARAMETER_PRUNING)) {
			return new TreebasedPessimisticPruner(getParameterAsDouble(PARAMETER_CONFIDENCE), null);
		} else {
			return null;
		}
	}

	@Override
	public List<ColumnTerminator> getTerminationCriteria(ExampleSet exampleSet) throws OperatorException {
		List<ColumnTerminator> result = new LinkedList<>();
		result.add(new ColumnSingleLabelTermination());
		result.add(new ColumnNoAttributeLeftTermination());
		result.add(new ColumnEmptyTermination());
		int maxDepth = getParameterAsInt(PARAMETER_MAXIMAL_DEPTH);
		if (maxDepth <= 0) {
			maxDepth = exampleSet.size();
		}
		result.add(new ColumnMaxDepthTermination(maxDepth));

		logger.info("Parallel decision tree get termination criteria of example set[%s]", exampleSet.getName());

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
	protected AbstractParallelTreeBuilder getTreeBuilder(ExampleSet exampleSet) throws OperatorException {
		if (Resources.getConcurrencyContext(this).getParallelism() > 1) {
			return new ConcurrentTreeBuilder(this, createCriterion(), getTerminationCriteria(exampleSet), getPruner(),
					getSplitPreprocessing(0), getParameterAsBoolean(PARAMETER_PRE_PRUNING),
					getParameterAsInt(PARAMETER_NUMBER_OF_PREPRUNING_ALTERNATIVES),
					getParameterAsInt(PARAMETER_MINIMAL_SIZE_FOR_SPLIT), getParameterAsInt(PARAMETER_MINIMAL_LEAF_SIZE));
		}
		return new NonParallelTreeBuilder(this, createCriterion(), getTerminationCriteria(exampleSet), getPruner(),
				getSplitPreprocessing(0), getParameterAsBoolean(PARAMETER_PRE_PRUNING),
				getParameterAsInt(PARAMETER_NUMBER_OF_PREPRUNING_ALTERNATIVES),
				getParameterAsInt(PARAMETER_MINIMAL_SIZE_FOR_SPLIT), getParameterAsInt(PARAMETER_MINIMAL_LEAF_SIZE));
	}

	@Override
	public String getDefaultFullName() {
		return I18N.getMessage("pio.ParallelDecisionTreeLearner.name");
	}

	@Override
	public String getDescription() {
		return I18N.getMessage("pio.ParallelDecisionTreeLearner.description");
	}

	@Override
	public OperatorGroup getGroup() {
		return OperatorGroup.algorithmModel;
	}

	@Override
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = super.getParameterTypes();

		return types;

	}

}
