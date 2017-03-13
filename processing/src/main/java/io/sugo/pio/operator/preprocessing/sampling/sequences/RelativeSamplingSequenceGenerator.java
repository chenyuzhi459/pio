package io.sugo.pio.operator.preprocessing.sampling.sequences;

import io.sugo.pio.tools.RandomGenerator;


/**
 * This class provides a relative sampling sequence. The given fraction will be part of the sample.
 * This is achieved by utilizing the {@link AbsoluteSamplingSequenceGenerator} with the absolute
 * number derived from fraction and source size.
 * 
 */
public class RelativeSamplingSequenceGenerator extends AbsoluteSamplingSequenceGenerator {

	public RelativeSamplingSequenceGenerator(int source, double fraction, RandomGenerator random) {
		super(source, (int) (source * fraction), random);
	}
}
