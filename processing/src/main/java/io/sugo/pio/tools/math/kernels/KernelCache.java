package io.sugo.pio.tools.math.kernels;

/**
 * This is the interface describing the kernel cache.
 * 
 */
public interface KernelCache {

	/**
	 * Stores the value. Should only be invoked if the value was not known. This method might delete
	 * kernel values so that the invocation of get might deliver NaN.
	 */
	public void store(int i, int j, double value);

	/** Delivers the cached value or Double.NaN if the value is not known. */
	public double get(int i, int j);

}
