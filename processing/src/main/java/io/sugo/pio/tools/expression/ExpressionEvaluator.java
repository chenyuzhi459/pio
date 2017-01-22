package io.sugo.pio.tools.expression;


import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Interface for a container that holds Represents intermediate results when building a
 * {@link Expression} via a {@link ExpressionParser}.
 *
 * @author Gisa Schaefer
 * @since 6.5.0
 */
public interface ExpressionEvaluator {

	/**
	 * Returns the {@link ExpressionType}.
	 *
	 * @return the type of the expression
	 */
	public ExpressionType getType();

	/**
	 * Returns whether the result of the callable in this container is always constant
	 *
	 * @return {@code true} if the result of the callable is constant
	 */
	public boolean isConstant();

	/**
	 * Returns the stored String callable, can be {@code null} if the type is not compatible with a
	 * String callable.
	 *
	 * @return a String callable, can be {@code null}
	 */
	public Callable<String> getStringFunction();

	/**
	 * Returns the stored double callable, can be {@code null} if the type is not compatible with a
	 * double callable.
	 *
	 * @return a double callable, can be {@code null}
	 */
	public DoubleCallable getDoubleFunction();

	/**
	 * Returns the stored Date callable, can be {@code null} if the type is not compatible with a
	 * Date callable.
	 *
	 * @return a Date callable, can be {@code null}
	 */
	public Callable<Date> getDateFunction();

	/**
	 * Returns the stored Boolean callable, can be {@code null} if the type is not compatible with a
	 * Boolean callable.
	 *
	 * @return a Boolean callable, can be {@code null}
	 */
	public Callable<Boolean> getBooleanFunction();

}
