package io.sugo.pio.operator.learner.tree;


import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.OperatorCapability;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.OperatorGroup;

import java.util.LinkedList;
import java.util.List;

/**
 * This operator learns decision trees without pruning using both nominal and numerical attributes.
 * Decision trees are powerful classification methods which often can also easily be understood.
 * This decision tree learner works similar to Quinlan's ID3.
 *
 * @author Ingo Mierswa
 *
 * @deprecated This learner is not used anymore.
 */
@Deprecated
public class ID3NumericalLearner extends AbstractTreeLearner {

	@Override
	public String getName() {
		return "ID3NumericalLearner";
	}

	@Override
	public String getFullName() {
		return "ID3NumericalLearner";
	}

	@Override
	public String getDescription() {
		return "ID3NumericalLearner";
	}

	@Override
	public OperatorGroup getGroup() {
		return OperatorGroup.algorithmModel;
	}

	@Override
	public Pruner getPruner() throws OperatorException {
		return null;
	}

	@Override
	public List<Terminator> getTerminationCriteria(ExampleSet exampleSet) {
		List<Terminator> result = new LinkedList<Terminator>();
		result.add(new SingleLabelTermination());
		result.add(new NoAttributeLeftTermination());
		result.add(new EmptyTermination());
		result.add(new MaxDepthTermination(exampleSet.size()));
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
				true, 0, getParameterAsInt(PARAMETER_MINIMAL_SIZE_FOR_SPLIT), getParameterAsInt(PARAMETER_MINIMAL_LEAF_SIZE));
	}
}
