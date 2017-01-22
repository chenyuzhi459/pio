package io.sugo.pio.example.set;


import io.sugo.pio.example.Example;

import java.util.Iterator;

/**
 * This example reader is based on the given mapping and skips all examples which are not part of
 * the mapping. If an index occurs more than once the example will be returned the number of desired
 * times. For performance reasons the mapping array must have been sorted beforehand.
 * 
 * @author Ingo Mierswa, Martin Scholz
 */
public class MappedExampleReader extends AbstractExampleReader {

	/** The example reader that provides a complete example set. */
	private Iterator<Example> parent;

	/** The used mapping. */
	private int[] mapping;

	/** The example that will be returned by the next invocation of next(). */
	private Example currentExample;

	/** Indicates if the current example was &quot;delivered&quot; by a call of {@link #next()}. */
	private boolean nextInvoked = true;

	/** The current index in the mapping. */
	private int index = -1;

	/** Constructs a new mapped example reader. */
	public MappedExampleReader(Iterator<Example> parent, int[] mapping) {
		this.parent = parent;
		this.currentExample = null;
		this.mapping = mapping;
	}

	@Override
	public boolean hasNext() {
		if (this.nextInvoked) {
			this.nextInvoked = false;
			int oldMapping = -1;
			if (index >= this.mapping.length - 1) {
				return false;
			}
			if (index != -1) {
				oldMapping = this.mapping[index];
			}
			this.index++;
			int newMapping = this.mapping[index];

			if (newMapping != oldMapping) {
				do {
					if (!parent.hasNext()) {
						return false;
					}
					this.currentExample = parent.next();
					oldMapping++;
				} while (oldMapping < newMapping);
			} else {
				return true;
			}
		}
		return true;
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
