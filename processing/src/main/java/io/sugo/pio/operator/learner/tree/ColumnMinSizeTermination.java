package io.sugo.pio.operator.learner.tree;

/**
 * Terminates if the selected examples are less than minSize examples.
 *
 * @author Sebastian Land, Ingo Mierswa, Gisa Schaefer
 */
public class ColumnMinSizeTermination implements ColumnTerminator {

	private int minSize;

	public ColumnMinSizeTermination(int minSize) {
		this.minSize = minSize;
	}

	@Override
	public boolean shouldStop(int[] selectedExamples, int[] selectedAttributes, ColumnExampleTable columnTable, int depth) {
		return selectedExamples.length < this.minSize;
	}
}
