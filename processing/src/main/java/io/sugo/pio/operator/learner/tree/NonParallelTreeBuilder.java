package io.sugo.pio.operator.learner.tree;


import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.learner.tree.criterions.ColumnCriterion;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;
import java.util.Map;

/**
 * Build a tree from an example set, not in parallel.
 *
 * @author Gisa Schaefer
 */
public class NonParallelTreeBuilder extends AbstractParallelTreeBuilder {

	/**
	 * Pipes the arguments to the super constructor and sets an additional parameter forbidding
	 * parallel table creation.
	 */
	public NonParallelTreeBuilder(Operator operator, ColumnCriterion criterion, List<ColumnTerminator> terminationCriteria,
								  Pruner pruner, AttributePreprocessing preprocessing, boolean prePruning, int numberOfPrepruningAlternatives,
								  int minSizeForSplit, int minLeafSize) {
		super(operator, criterion, terminationCriteria, pruner, preprocessing, prePruning, numberOfPrepruningAlternatives,
				minSizeForSplit, minLeafSize, false);
	}

	@Override
	void startTree(Tree root, Map<Integer, int[]> allSelectedExamples, int[] selectedAttributes, int depth)
			throws OperatorException {
		NodeData rootNode = new NodeData(root, allSelectedExamples, selectedAttributes, depth);
		Deque<NodeData> queue = new ArrayDeque<>();
		queue.push(rootNode);
		while (!queue.isEmpty()) {
			queue.addAll(splitNode(queue.pop(), false));
		}

	}

	@Override
	boolean doStartSelectionInParallel() {
		return false;
	}

}
