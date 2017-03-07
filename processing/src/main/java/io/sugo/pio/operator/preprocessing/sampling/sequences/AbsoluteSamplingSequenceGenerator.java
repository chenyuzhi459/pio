package io.sugo.pio.operator.preprocessing.sampling.sequences;

import io.sugo.pio.tools.RandomGenerator;


/**
 * This sampling sequence guarantees that the resulting sequence will contain either all elements or
 * exactly as much as given as target.
 * 
 */
public class AbsoluteSamplingSequenceGenerator extends SamplingSequenceGenerator {

	private int toCome;

	private int toAccept;

	public AbsoluteSamplingSequenceGenerator(int source, int target, RandomGenerator random) {
		super(random);
		this.toCome = source;
		this.toAccept = target;
	}

	@Override
	public boolean useNext() {
		boolean accept = (random.nextInt(toCome) + 1) <= toAccept;
		if (accept) {
			toAccept--;
		}
		toCome--;
		return accept;
	}
}
