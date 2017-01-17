package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.OperatorCapability;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.parameter.ParameterType;

import java.util.LinkedList;
import java.util.List;

/**
 * This operator learns decision stumps, i.e. a small decision tree with only one single split. This
 * decision stump works on both numerical and nominal attributes.
 * 
 * @author Ingo Mierswa
 */
public class DecisionStumpLearner extends AbstractTreeLearner {

	public DecisionStumpLearner() {
		super("decisionStumpLearner");
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
		result.add(new MaxDepthTermination(2));
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
	public List<ParameterType> getParameterTypes() {
		List<ParameterType> types = new LinkedList<ParameterType>();
		for (ParameterType type : super.getParameterTypes()) {
			if (type.getKey().equals(PARAMETER_MINIMAL_LEAF_SIZE)) {
				type.setDefaultValue(Integer.valueOf(1));
			}

			if (!type.getKey().equals(PARAMETER_MINIMAL_GAIN) && !type.getKey().equals(PARAMETER_MINIMAL_SIZE_FOR_SPLIT)) {
				types.add(type);
			}
		}
		return types;
	}

	@Override
	protected TreeBuilder getTreeBuilder(ExampleSet exampleSet) throws OperatorException {
		return new TreeBuilder(createCriterion(0.0), getTerminationCriteria(exampleSet), getPruner(),
				getSplitPreprocessing(), new DecisionTreeLeafCreator(), true, 0, 0,
				getParameterAsInt(PARAMETER_MINIMAL_LEAF_SIZE));
	}
}
