package io.sugo.pio.operator.learner.tree;

/**
 * Implementations of this interface are used in order to determine if a splitting procedure should
 * be stopped.
 *
 * @author Sebastian Land, Ingo Mierswa, Gisa Schaefer
 * @since 6.2.000
 */
public interface ColumnTerminator {

	public boolean shouldStop(int[] selectedExamples, int[] selectedAttributes, ColumnExampleTable columnTable, int depth);

}
