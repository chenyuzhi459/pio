package io.sugo.pio.tools.expression;

/**
 * Callable that has a call-method returning a primitive double.
 *
 * @author Gisa Schaefer
 * @since 6.5.0
 */
public interface DoubleCallable {

	/**
	 * Computes a result, or throws an exception if unable to do so.
	 *
	 * @return computed result
	 * @throws Exception
	 *             if unable to compute a result
	 */
	public double call() throws Exception;
}
