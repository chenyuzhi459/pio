package io.sugo.pio.operator.preprocessing.sampling.sequences;

import io.sugo.pio.tools.RandomGenerator;


/**
 * This class provides a sampling sequence, where each element will have the given probability to be
 * part of the sample.
 * 
 */
public class ProbabilitySamplingSequenceGenerator extends SamplingSequenceGenerator {

	private double fraction;

	public ProbabilitySamplingSequenceGenerator(double fraction, RandomGenerator random) {
		super(random);
		this.fraction = fraction;
	}

	@Override
	public boolean useNext() {
		return random.nextDouble() < fraction;
	}
}
