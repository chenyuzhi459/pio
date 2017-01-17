package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;

import java.util.Iterator;

/**
 * This criterion terminates if only one single label is left.
 * 
 * @author Sebastian Land, Ingo Mierswa
 */
public class SingleLabelTermination implements Terminator {

	public SingleLabelTermination() {}

	@Override
	public boolean shouldStop(ExampleSet exampleSet, int depth) {
		Attribute label = exampleSet.getAttributes().getLabel();
		Iterator<Example> iterator = exampleSet.iterator();
		if (label != null && iterator.hasNext()) {
			double singleValue = iterator.next().getValue(label);
			while (iterator.hasNext()) {
				if (iterator.next().getValue(label) != singleValue) {
					return false;
				}
			}
		}
		return true;
	}
}
