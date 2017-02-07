package io.sugo.pio.operator.learner.tree;

/**
 * Splitting should be terminated if there are no selected examples.
 *
 * @author Ingo Mierswa, Gisa Schaefer
 */
public class ColumnEmptyTermination implements ColumnTerminator {

	@Override
	public boolean shouldStop(int[] selectedExamples, int[] selectedAttributes, ColumnExampleTable columnTable, int depth) {
		return selectedExamples.length == 0;
	}
}
