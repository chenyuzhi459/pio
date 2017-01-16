package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.ExampleSet;

/**
 * Implementations of this interface are used in order to determine if a splitting procedure should
 * be stopped.
 * 
 * @author Sebastian Land, Ingo Mierswa
 */
public interface Terminator {

	public boolean shouldStop(ExampleSet exampleSet, int depth);

}
