package io.sugo.pio.operator.learner.tree;

/**
 * Terminates if the example set does not have any regular attributes.
 *
 * @author Sebastian Land, Ingo Mierswa, Gisa Schaefer
 */
public class ColumnNoAttributeLeftTermination implements ColumnTerminator {

	@Override
	public boolean shouldStop(int[] selectedExamples, int[] selectedAttributes, ColumnExampleTable columnTable, int depth) {
		return selectedAttributes.length == 0;
	}

}
