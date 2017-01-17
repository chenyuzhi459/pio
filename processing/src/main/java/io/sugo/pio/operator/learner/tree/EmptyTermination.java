package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.ExampleSet;

/**
 * Splitting should be terminated if the example set is empty.
 * 
 * @author Ingo Mierswa
 */
public class EmptyTermination implements Terminator {

	@Override
	public boolean shouldStop(ExampleSet exampleSet, int depth) {
		return exampleSet.size() == 0;
	}
}
