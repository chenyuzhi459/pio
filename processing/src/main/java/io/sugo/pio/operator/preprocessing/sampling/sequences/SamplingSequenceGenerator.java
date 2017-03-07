package io.sugo.pio.operator.preprocessing.sampling.sequences;

import io.sugo.pio.tools.RandomGenerator;


/**
 * This is an abstract super class of all sampling sequence generators. Subclasses of this class
 * will return a sequence of true/false values to indicate that the next example to come should be
 * part of the sample or not.
 * 
 */
public abstract class SamplingSequenceGenerator {

	protected RandomGenerator random;

	protected SamplingSequenceGenerator(RandomGenerator random) {
		this.random = random;
	}

	/**
	 * This method has to be overridden. Subclasses must implement this method, so that it returns
	 * true if the next example should be part of the sample or no otherwise.
	 */
	public abstract boolean useNext();
}
