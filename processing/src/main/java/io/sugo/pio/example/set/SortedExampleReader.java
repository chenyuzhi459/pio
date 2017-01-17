package io.sugo.pio.example.set;

import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;

/**
 * This example reader is based on the given mapping and skips all examples which are not part of
 * the mapping. This implementation is quite inefficient on databases and other non-memory example
 * tables and should therefore only be used for small data sets.
 * 
 * @author Ingo Mierswa, Sebastian Land
 */
public class SortedExampleReader extends AbstractExampleReader {

	/** The parent example set. */
	private ExampleSet parent;

	/** The current index in the mapping. */
	private int currentIndex;

	/** Indicates if the current example was &quot;delivered&quot; by a call of {@link #next()}. */
	private boolean nextInvoked = true;

	/** The example that will be returned by the next invocation of next(). */
	private Example currentExample = null;

	/** Constructs a new mapped example reader. */
	public SortedExampleReader(ExampleSet parent) {
		this.parent = parent;
		this.currentIndex = -1;
	}

	@Override
	public boolean hasNext() {
		if (this.nextInvoked) {
			this.nextInvoked = false;
			this.currentIndex++;
			if (this.currentIndex < parent.size()) {
				this.currentExample = this.parent.getExample(this.currentIndex);
				return true;
			} else {
				return false;
			}
		}
		return (this.currentIndex < parent.size());
	}

	@Override
	public Example next() {
		if (hasNext()) {
			this.nextInvoked = true;
			return currentExample;
		} else {
			return null;
		}
	}
}
