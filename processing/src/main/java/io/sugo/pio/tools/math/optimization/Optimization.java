package io.sugo.pio.tools.math.optimization;

import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.performance.PerformanceVector;

/**
 * General interface for all optimization methods. The method {@link #optimize()} should be invoked
 * to start the optimization process. The optimal result can be queried by the method
 * {@link #getBestValuesEver()}. The other methods of this interface can be used to support logging
 * or plotting.
 * 
 * @author Ingo Mierswa
 */
public interface Optimization {

	/**
	 * Should be invoked to start optimization. Since the optimization can use other (inner)
	 * operators to support fitness evaluation this method is allowed to throw OperatorExceptions.
	 */
	public void optimize() throws OperatorException;

	/** Returns the current generation. */
	public int getGeneration();

	/** Returns the best fitness in the current generation. */
	public double getBestFitnessInGeneration();

	/** Returns the best fitness ever. */
	public double getBestFitnessEver();

	/** Returns the best performance vector ever. */
	public PerformanceVector getBestPerformanceEver();

	/**
	 * Returns the best values ever. Use this method after optimization to get the best result.
	 */
	public double[] getBestValuesEver();
}
