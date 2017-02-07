package io.sugo.pio.operator.learner.tree;


import io.sugo.pio.example.Attribute;

/**
 * This class can be used to transform an inner tree node into a leaf.
 *
 * @author Gisa Schaefer
 */
public class ParallelDecisionTreeLeafCreator {

	/**
	 * Transforms the tree node into a leaf by storing the number of label values and naming the
	 * leaf by the majority label value.
	 *
	 * @param node
	 * @param columnTable
	 * @param selectedExamples
	 */
	public void changeTreeToLeaf(Tree node, ColumnExampleTable columnTable, int[] selectedExamples) {
		Attribute label = columnTable.getLabel();
		int[] labelColumn = columnTable.getLabelColumn();
		int numberOfLabels = label.getMapping().size();
		int[] labelValueCount = new int[numberOfLabels];
		// count the different labels for the example number in selection
		for (int i = 0; i < selectedExamples.length; i++) {
			int indexForAdd = labelColumn[selectedExamples[i]];
			labelValueCount[indexForAdd]++;
		}
		int maxcount = 0;
		String labelName = null;
		// save the frequency of different labels in the node and name it by the most frequent label
		for (String value : label.getMapping().getValues()) {
			int count = labelValueCount[label.getMapping().getIndex(value)];
			node.addCount(value, count);
			if (count > maxcount) {
				maxcount = count;
				labelName = value;
			}
		}
		node.setLeaf(labelName);
	}
}
