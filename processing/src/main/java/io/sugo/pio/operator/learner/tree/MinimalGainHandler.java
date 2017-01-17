package io.sugo.pio.operator.learner.tree;

/**
 * This interface indicates that the criterion is able to handle a minimal gain prepruning. In cases
 * where the minimal gain is not reached, a gain of 0 should be returned instead by the criterion.
 * 
 * @author Ingo Mierswa
 */
public interface MinimalGainHandler {

	public void setMinimalGain(double minimalGain);

}
