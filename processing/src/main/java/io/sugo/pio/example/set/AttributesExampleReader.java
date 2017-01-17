package io.sugo.pio.example.set;

import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;

import java.util.Iterator;

/**
 * This reader simply uses all examples from the parent and all available attributes.
 * 
 * @author Ingo Mierswa
 */
public class AttributesExampleReader extends AbstractExampleReader {

	/** The parent example reader. */
	private Iterator<Example> parent;

	/** The used attributes are described in this example set. */
	private ExampleSet exampleSet;

	/** Creates a simple example reader. */
	public AttributesExampleReader(Iterator<Example> parent, ExampleSet exampleSet) {
		this.parent = parent;
		this.exampleSet = exampleSet;
	}

	/** Returns true if there are more data rows. */
	@Override
	public boolean hasNext() {
		return this.parent.hasNext();
	}

	/** Returns a new example based on the current data row. */
	@Override
	public Example next() {
		if (!hasNext()) {
			return null;
		}
		Example example = this.parent.next();
		if (example == null) {
			return null;
		}
		return new Example(example.getDataRow(), exampleSet);
	}
}
