package io.sugo.pio.tools.expression.internal;


import io.sugo.pio.tools.expression.DoubleCallable;
import io.sugo.pio.tools.expression.ExpressionEvaluator;
import io.sugo.pio.tools.expression.ExpressionType;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * {@link ExpressionEvaluator} that supplies constructors for all admissible combinations of its
 * fields. It checks if the required relationship between {@link ExpressionType} and the Callable
 * that is not {@code null} is satisfied.
 *
 * @author Gisa Schaefer
 *
 */
public class SimpleExpressionEvaluator implements ExpressionEvaluator {

	private final Callable<String> stringCallable;
	private final Callable<Date> dateCallable;
	private final DoubleCallable doubleCallable;
	private final Callable<Boolean> booleanCallable;

	private final ExpressionType type;
	private final boolean isConstant;

	/**
	 * Initializes the fields.
	 */
	protected SimpleExpressionEvaluator(ExpressionType type, Callable<String> stringCallable, DoubleCallable doubleCallable,
										Callable<Boolean> booleanCallable, Callable<Date> dateCallable, boolean isConstant) {
		this.stringCallable = stringCallable;
		this.dateCallable = dateCallable;
		this.doubleCallable = doubleCallable;
		this.booleanCallable = booleanCallable;
		this.type = type;
		this.isConstant = isConstant;
	}

	/**
	 * Creates an {@link ExpressionEvaluator} with the given data where the other callables are
	 * {@code null}. type must be ExpressionType.INTEGER or ExpressionType.DOUBLE.
	 *
	 * @param doubleCallable
	 *            the callable to store
	 * @param type
	 *            the type of the result of the callable, must be ExpressionType.INTEGER or
	 *            ExpressionType.DOUBLE
	 * @param isConstant
	 *            whether the result of the callable is constant
	 */
	public SimpleExpressionEvaluator(DoubleCallable doubleCallable, ExpressionType type, boolean isConstant) {
		this(type, null, doubleCallable, null, null, isConstant);
		if (type != ExpressionType.DOUBLE && type != ExpressionType.INTEGER) {
			throw new IllegalArgumentException("Invalid type " + type + "for Callable");
		}
	}

	/**
	 * Creates an {@link ExpressionEvaluator} with the given data where the other callables are
	 * {@code null}. type must be ExpressionType.STRING.
	 *
	 * @param stringCallable
	 *            the callable to store
	 * @param type
	 *            the type of the result of the callable, must be ExpressionType.STRING
	 * @param isConstant
	 *            whether the result of the callable is constant
	 */
	public SimpleExpressionEvaluator(Callable<String> stringCallable, ExpressionType type, boolean isConstant) {
		this(type, stringCallable, null, null, null, isConstant);
		if (type != ExpressionType.STRING) {
			throw new IllegalArgumentException("Invalid type " + type + "for Callable");
		}
	}

	/**
	 * Creates an {@link ExpressionEvaluator} with the given data where the other callables are
	 * {@code null}. type must be ExpressionType.STRING.
	 *
	 * @param dateCallable
	 *            the callable to store
	 * @param type
	 *            the type of the result of the callable, must be ExpressionType.DATE
	 * @param isConstant
	 *            whether the result of the callable is constant
	 */
	public SimpleExpressionEvaluator(ExpressionType type, Callable<Date> dateCallable, boolean isConstant) {
		this(type, null, null, null, dateCallable, isConstant);
		if (type != ExpressionType.DATE) {
			throw new IllegalArgumentException("Invalid type " + type + "for Callable");
		}
	}

	/**
	 * Creates an {@link ExpressionEvaluator} with the given data where the other callables are
	 * {@code null}. type must be ExpressionType.BOOLEAN.
	 *
	 * @param booleanCallable
	 *            the callable to store
	 * @param type
	 *            the type of the result of the callable, must be ExpressionType.BOOLEAN
	 * @param isConstant
	 *            whether the result of the callable is constant
	 */
	public SimpleExpressionEvaluator(Callable<Boolean> booleanCallable, boolean isConstant, ExpressionType type) {
		this(type, null, null, booleanCallable, null, isConstant);
		if (type != ExpressionType.BOOLEAN) {
			throw new IllegalArgumentException("Invalid type " + type + "for Callable");
		}
	}

	/**
	 * Creates an {@link ExpressionEvaluator} with a {@link DoubleCallable} returning constantly
	 * doubleValue. type must be ExpressionType.INTEGER or ExpressionType.DOUBLE.
	 *
	 * @param doubleValue
	 *            the constant double return value
	 * @param type
	 *            the type of the result of the callable, must be ExpressionType.INTEGER or
	 *            ExpressionType.DOUBLE
	 */
	public SimpleExpressionEvaluator(double doubleValue, ExpressionType type) {
		this(makeConstantCallable(doubleValue), type, true);
	}

	/**
	 * Creates an {@link ExpressionEvaluator} with a {@link Callable<String>} returning constantly
	 * stringValue. type must be ExpressionType.STRING.
	 *
	 * @param stringValue
	 *            the constant String return value
	 * @param type
	 *            the type of the result of the callable, must be ExpressionType.STRING
	 */
	public SimpleExpressionEvaluator(String stringValue, ExpressionType type) {
		this(makeConstantCallable(stringValue), type, true);
	}

	/**
	 * Creates an {@link ExpressionEvaluator} with a {@link Callable<Boolean>} returning constantly
	 * booleanValue.
	 *
	 * @param booleanValue
	 *            the constant Boolean return value
	 * @param type
	 *            the type of the result of the callable, must be ExpressionType.BOOLEAN
	 */
	public SimpleExpressionEvaluator(Boolean booleanValue, ExpressionType type) {
		this(makeConstantCallable(booleanValue), true, type);
	}

	/**
	 * Creates an {@link ExpressionEvaluator} with a {@link Callable<Date>} returning constantly
	 * dateValue.
	 *
	 * @param dateValue
	 *            the constant Date return value
	 * @param type
	 *            type the type of the result of the callable, must be ExpressionType.DATE
	 */
	public SimpleExpressionEvaluator(Date dateValue, ExpressionType type) {
		this(type, makeConstantCallable(dateValue), true);
	}

	private static DoubleCallable makeConstantCallable(final double doubleValue) {
		return new DoubleCallable() {

			@Override
			public double call() {
				return doubleValue;
			}

		};
	}

	private static Callable<Boolean> makeConstantCallable(final Boolean booleanValue) {
		return new Callable<Boolean>() {

			@Override
			public Boolean call() {
				return booleanValue;
			}

		};
	}

	private static Callable<String> makeConstantCallable(final String stringValue) {
		return new Callable<String>() {

			@Override
			public String call() {
				return stringValue;
			}

		};
	}

	private static Callable<Date> makeConstantCallable(final Date dateValue) {
		return new Callable<Date>() {

			@Override
			public Date call() {
				return dateValue;
			}

		};
	}

	@Override
	public ExpressionType getType() {
		return type;
	}

	@Override
	public boolean isConstant() {
		return isConstant;
	}

	@Override
	public Callable<String> getStringFunction() {
		return stringCallable;
	}

	@Override
	public Callable<Date> getDateFunction() {
		return dateCallable;
	}

	@Override
	public DoubleCallable getDoubleFunction() {
		return doubleCallable;
	}

	@Override
	public Callable<Boolean> getBooleanFunction() {
		return booleanCallable;
	}

}
