package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.ExampleSet;

/**
 * Will be used before each split.
 * 
 * @author Ingo Mierswa
 */
public interface SplitPreprocessing {

	/** Will be invoked before each new split. */
	public ExampleSet preprocess(ExampleSet exampleSet);

}
