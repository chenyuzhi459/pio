package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Statistics;
import io.sugo.pio.tools.math.MathFunctions;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class provides a pruner based on some heuristic statistics. It cuts the tree to reduce
 * overfitting.
 * 
 * @author Sebastian Land, Ingo Mierswa
 */
public class PessimisticPruner implements Pruner {

	private static final double PRUNE_PREFERENCE = 0.001;

	private double confidenceLevel;

	private LeafCreator leafCreator;

	public PessimisticPruner(double confidenceLevel, LeafCreator leafCreator) {
		this.confidenceLevel = confidenceLevel;
		this.leafCreator = leafCreator;
	}

	@Override
	public void prune(Tree root) {
		Iterator<Edge> childIterator = root.childIterator();
		while (childIterator.hasNext()) {
			pruneChild(childIterator.next().getChild(), root);
		}
	}

	private void pruneChild(Tree currentNode, Tree father) {
		// going down to fathers of leafs
		if (!currentNode.isLeaf()) {
			Iterator<Edge> childIterator = currentNode.childIterator();
			while (childIterator.hasNext()) {
				pruneChild(childIterator.next().getChild(), currentNode);
			}
			if (!childrenHaveChildren(currentNode)) {
				// calculating error estimate for leafs
				double leafsErrorEstimate = 0;
				childIterator = currentNode.childIterator();
				Set<String> classSet = new HashSet<String>();
				while (childIterator.hasNext()) {
					Tree leafNode = childIterator.next().getChild();
					ExampleSet leafExampleSet = leafNode.getTrainingSet();
					classSet.add(leafNode.getLabel());
					int examples = leafExampleSet.size();
					double currentErrorRate = getErrorNumber(leafExampleSet, leafExampleSet.getAttributes().getLabel()
							.getMapping().getIndex(leafNode.getLabel()))
							/ (double) leafExampleSet.size();
					;
					leafsErrorEstimate += pessimisticErrors(examples, currentErrorRate, confidenceLevel)
							* (((double) examples) / currentNode.getTrainingSet().size());
				}

				// calculating error estimate for current node
				ExampleSet currentNodeExampleSet = currentNode.getTrainingSet();
				if (classSet.size() <= 1) {
					currentNode.removeChildren();
					leafCreator.changeTreeToLeaf(currentNode, currentNodeExampleSet);
				} else {
					double currentNodeLabel = prunedLabel(currentNodeExampleSet);
					int examples = currentNodeExampleSet.size();
					double currentErrorRate = getErrorNumber(currentNodeExampleSet, currentNodeLabel)
							/ (double) currentNodeExampleSet.size();
					double nodeErrorEstimate = pessimisticErrors(examples, currentErrorRate, confidenceLevel);
					// if currentNode error level is less than children: prune

					if (nodeErrorEstimate - PRUNE_PREFERENCE <= leafsErrorEstimate) {
						currentNode.removeChildren();
						leafCreator.changeTreeToLeaf(currentNode, currentNodeExampleSet);
					}
				}
			}
		}
	}

	private boolean childrenHaveChildren(Tree node) {
		Iterator<Edge> iterator = node.childIterator();
		while (iterator.hasNext()) {
			if (!iterator.next().getChild().isLeaf()) {
				return true;
			}
		}
		return false;
	}

	private int getErrorNumber(ExampleSet exampleSet, double label) {
		int errors = 0;
		Iterator<Example> iterator = exampleSet.iterator();
		while (iterator.hasNext()) {
			if (iterator.next().getLabel() != label) {
				errors++;
			}
		}
		return errors;
	}

	public double prunedLabel(ExampleSet exampleSet) {
		Attribute labelAttribute = exampleSet.getAttributes().getLabel();
		exampleSet.recalculateAttributeStatistics(labelAttribute);
		double test = exampleSet.getStatistics(labelAttribute, Statistics.MODE);
		return test;
	}

	// calculates the pessimistic number of errors, using some confidence level.
	public double pessimisticErrors(double numberOfExamples, double errorRate, double confidenceLevel) {
		if (errorRate < 1E-6) {
			return errorRate + numberOfExamples * (1.0 - Math.exp(Math.log(confidenceLevel) / numberOfExamples));
		} else if (errorRate + 0.5 >= numberOfExamples) {
			return errorRate + 0.67 * (numberOfExamples - errorRate);
		} else {
			double coefficient = MathFunctions.normalInverse(1 - confidenceLevel);
			coefficient *= coefficient;
			double pessimisticRate = (errorRate + 0.5 + coefficient / 2.0d + Math.sqrt(coefficient
					* ((errorRate + 0.5) * (1 - (errorRate + 0.5) / numberOfExamples) + coefficient / 4.0d)))
					/ (numberOfExamples + coefficient);
			return (numberOfExamples * pessimisticRate);
		}
	}
}
