package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.ExampleSet;

/**
 * Terminates if the example set does not have any regular attributes.
 * 
 * @author Sebastian Land, Ingo Mierswa
 */
public class NoAttributeLeftTermination implements Terminator {

	public NoAttributeLeftTermination() {}

	@Override
	public boolean shouldStop(ExampleSet exampleSet, int depth) {
		return (exampleSet.getAttributes().size() == 0);
	}
}
