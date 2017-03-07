package io.sugo.pio.operator.performance;

/**
 * Performance criteria implementing this interface are able to calculate a performance measurement
 * based on given class weights.
 * 
 */
public interface ClassWeightedPerformance {

	/**
	 * Sets the weights. Please note that the given array might also be null. Even if the method is
	 * not invoked at all the performance criterion should return an (unweighted) performance
	 * estimation. If the array is not null, the only requirement is that the sum of all weights
	 * must not be 0.
	 */
	public void setWeights(double[] weights);

}
