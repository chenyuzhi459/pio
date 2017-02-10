package io.sugo.pio.operator.learner.tree;

/**
 * Will be used before each split in {@link AbstractParallelTreeBuilder#splitNode}.
 *
 * @author Ingo Mierswa, Gisa Schaefer
 * @since 6.2.000
 */
public interface AttributePreprocessing {

	/** Will be invoked before each new split. */
	public int[] preprocess(int[] attributeSelection);

}
