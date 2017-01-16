package io.sugo.pio.example.set;


import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;

/**
 * Returns only a subset of an example set specified by an instance of {@link Partition}.
 * 
 * @author Simon Fischer, Ingo Mierswa ingomierswa Exp $
 */
public class IndexBasedExampleSetReader extends AbstractExampleReader {

	/** Index of the current example. */
	private int current;

	private ExampleSet parent;

	/** The next example that will be returned. */
	private Example next;

	private int size;

	public IndexBasedExampleSetReader(ExampleSet parent) {
		this.parent = parent;
		this.size = parent.size();
		current = -1;
		hasNext();
	}

	@Override
	public boolean hasNext() {
		while (next == null) {
			current++;

			if (current >= size) {
				return false;
			}

			next = parent.getExample(current);
		}
		return true;
	}

	@Override
	public Example next() {
		if (!hasNext()) {
			return null;
		} else {
			Example dummy = next;
			next = null;
			return dummy;
		}
	}

}
