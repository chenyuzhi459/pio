package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.Statistics;

/**
 * This class can be used to transform an inner tree node into a leaf.
 * 
 * @author Ingo Mierswa, Christian Bockermann
 */
public class DecisionTreeLeafCreator implements LeafCreator {

	@Override
	public void changeTreeToLeaf(Tree node, ExampleSet exampleSet) {
		Attribute label = exampleSet.getAttributes().getLabel();
		exampleSet.recalculateAttributeStatistics(label);
		int labelValue = (int) exampleSet.getStatistics(label, Statistics.MODE);
		String labelName = label.getMapping().mapIndex(labelValue);
		node.setLeaf(labelName);
		for (String value : label.getMapping().getValues()) {
			int count = (int) exampleSet.getStatistics(label, Statistics.COUNT, value);
			node.addCount(value, count);
		}
	}
}
