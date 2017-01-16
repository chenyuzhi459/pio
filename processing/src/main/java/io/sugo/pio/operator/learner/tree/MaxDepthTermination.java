package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.ExampleSet;

/**
 * Terminates if a maximal depth is reached.
 * 
 * @author Ingo Mierswa
 */
public class MaxDepthTermination implements Terminator {

	private int maxDepth;

	public MaxDepthTermination(int maxDepth) {
		this.maxDepth = maxDepth;
	}

	@Override
	public boolean shouldStop(ExampleSet exampleSet, int depth) {
		return depth >= this.maxDepth;
	}
}
