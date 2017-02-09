package io.sugo.pio.operator.learner.tree;

/**
 * This criterion terminates if only one single label is left.
 *
 * @author Sebastian Land, Ingo Mierswa, Gisa Schaefer
 */
public class ColumnSingleLabelTermination implements ColumnTerminator {

	@Override
	public boolean shouldStop(int[] selectedExamples, int[] selectedAttributes, ColumnExampleTable columnTable, int depth) {
		int[] labelColumn = columnTable.getLabelColumn();
		int singleValue = labelColumn[selectedExamples[0]];
		for (int i : selectedExamples) {
			if (singleValue != labelColumn[i]) {
				return false;
			}
		}
		return true;
	}

}
