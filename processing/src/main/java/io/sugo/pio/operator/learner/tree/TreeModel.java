package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.ExampleSetUtilities;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.learner.SimplePredictionModel;

import java.util.Iterator;
import java.util.Map;

/**
 * The tree model is the model created by all decision trees.
 *
 * @author Sebastian Land
 */
public class TreeModel extends SimplePredictionModel {

	private static final long serialVersionUID = 4368631725370998591L;

	private Tree root;

	public TreeModel(ExampleSet exampleSet, Tree root) {
		super(exampleSet, ExampleSetUtilities.SetsCompareOption.ALLOW_SUPERSET,
				ExampleSetUtilities.TypesCompareOption.ALLOW_SAME_PARENTS);
		this.root = root;
	}

	public Tree getRoot() {
		return this.root;
	}

	@Override
	public double predict(Example example) throws OperatorException {
		return predict(example, root);
	}

	private double predict(Example example, Tree node) {
		if (node.isLeaf()) {
			int[] counts = new int[getLabel().getMapping().size()];
			int sum = 0;
			for (Map.Entry<String, Integer> entry : node.getCounterMap().entrySet()) {
				int count = entry.getValue();
				int index = getLabel().getMapping().getIndex(entry.getKey());
				counts[index] = count;
				sum += count;
			}
			for (int i = 0; i < counts.length; i++) {
				example.setConfidence(getLabel().getMapping().mapIndex(i), (double) counts[i] / sum);
			}
			return getLabel().getMapping().getIndex(node.getLabel());
		} else {
			Iterator<Edge> childIterator = node.childIterator();
			while (childIterator.hasNext()) {
				Edge edge = childIterator.next();
				SplitCondition condition = edge.getCondition();
				if (condition.test(example)) {
					return predict(example, edge.getChild());
				}
			}

			// nothing known from training --> use majority class in this node
			String majorityClass = null;
			int majorityCounter = -1;
			int[] counts = new int[getLabel().getMapping().size()];
			int sum = 0;
			for (Map.Entry<String, Integer> entry : node.getSubtreeCounterMap().entrySet()) {
				String className = entry.getKey();
				int count = entry.getValue().intValue();
				int index = getLabel().getMapping().getIndex(className);
				counts[index] = count;
				sum += count;
				if (count > majorityCounter) {
					majorityCounter = count;
					majorityClass = className;
				}
			}

			for (int i = 0; i < counts.length; i++) {
				example.setConfidence(getLabel().getMapping().mapIndex(i), (double) counts[i] / sum);
			}

			if (majorityClass != null) {
				return getLabel().getMapping().getIndex(majorityClass);
			} else {
				return 0;
			}
		}
	}

	@Override
	public String toString() {
		return this.root.toString();
	}
}
