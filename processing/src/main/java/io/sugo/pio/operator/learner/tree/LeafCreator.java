package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.ExampleSet;

/**
 * This class can be used to transform an inner tree node into a leaf.
 * 
 * @author Ingo Mierswa, Christian Bockermann
 */
public interface LeafCreator {

	public void changeTreeToLeaf(Tree node, ExampleSet exampleSet);
}
