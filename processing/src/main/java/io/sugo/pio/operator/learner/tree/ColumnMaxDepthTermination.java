package io.sugo.pio.operator.learner.tree;

/**
 * Terminates if a maximal depth is reached.
 *
 * @author Ingo Mierswa, Gisa Schaefer
 */
public class ColumnMaxDepthTermination implements ColumnTerminator {

	private int maxDepth;

	public ColumnMaxDepthTermination(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	@Override
	public boolean shouldStop(int[] selectedExamples, int[] selectedAttributes, ColumnExampleTable columnTable, int depth) {
		return depth >= this.maxDepth;
	}
}
