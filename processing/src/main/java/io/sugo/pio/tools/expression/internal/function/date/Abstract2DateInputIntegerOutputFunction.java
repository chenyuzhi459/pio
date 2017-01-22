package io.sugo.pio.tools.expression.internal.function.date;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.util.Date;
import java.util.concurrent.Callable;


/**
 * Abstract class for a {@link Function} that has two date arguments and returns an integer. The
 * function can has time zone and locale information as optional arguments.
 *
 * @author David Arnu
 *
 */
public abstract class Abstract2DateInputIntegerOutputFunction extends AbstractFunction {

	public Abstract2DateInputIntegerOutputFunction(String i18nKey, int numberOfArgumentsToCheck) {
		super(i18nKey, numberOfArgumentsToCheck, Ontology.INTEGER);
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {
		ExpressionType type = getResultType(inputEvaluators);

		if (inputEvaluators.length == 2) {

			ExpressionEvaluator left = inputEvaluators[0];
			ExpressionEvaluator right = inputEvaluators[1];
			return new SimpleExpressionEvaluator(makeDoubleCallable(left, right, null, null), type,
					isResultConstant(inputEvaluators));
		} else {
			ExpressionEvaluator left = inputEvaluators[0];
			ExpressionEvaluator right = inputEvaluators[1];
			ExpressionEvaluator locale = inputEvaluators[2];
			ExpressionEvaluator timeZone = inputEvaluators[3];
			return new SimpleExpressionEvaluator(makeDoubleCallable(left, right, locale, timeZone), type,
					isResultConstant(inputEvaluators));
		}

	}

	protected DoubleCallable makeDoubleCallable(ExpressionEvaluator left, ExpressionEvaluator right,
			ExpressionEvaluator locale, ExpressionEvaluator timeZone) {

		final Callable<Date> funcLeft = left.getDateFunction();
		final Callable<Date> funcRight = right.getDateFunction();
		final Callable<String> funcLocale;
		final Callable<String> funcTimeZone;

		if (locale != null) {
			funcLocale = locale.getStringFunction();
		} else {
			// create an dummy ExpressionEvaluator for the missing locale argument
			locale = new SimpleExpressionEvaluator("", ExpressionType.STRING);
			funcLocale = new Callable<String>() {

				@Override
				public String call() throws Exception {
					return null;
				}
			};
		}
		if (timeZone != null) {
			funcTimeZone = timeZone.getStringFunction();
		} else {
			// create an dummy ExpressionEvaluator for the missing time zone argument
			timeZone = new SimpleExpressionEvaluator("", ExpressionType.STRING);
			funcTimeZone = new Callable<String>() {

				@Override
				public String call() throws Exception {
					return null;
				}
			};
		}

		try {
			final Date valueLeft = left.isConstant() ? funcLeft.call() : null;
			final Date valueRight = right.isConstant() ? funcRight.call() : null;
			final String valueLocale = locale.isConstant() ? funcLocale.call() : null;
			final String valueTimezone = timeZone.isConstant() ? funcTimeZone.call() : null;

			// only check for common combinations of constant values

			// all constant values
			if (left.isConstant() && right.isConstant() && locale.isConstant() && timeZone.isConstant()) {
				final double result = compute(valueLeft, valueRight, valueLocale, valueTimezone);

				return new DoubleCallable() {

					@Override
					public double call() throws Exception {
						return result;
					}

				};
			} else if (left.isConstant() && !right.isConstant()) {
				// branch with constant locale and time zone data, probably both are constant or
				// both are not
				if (locale.isConstant() && timeZone.isConstant()) {
					return new DoubleCallable() {

						@Override
						public double call() throws Exception {
							return compute(valueLeft, funcRight.call(), valueLocale, valueTimezone);
						}
					};
				} else {
					return new DoubleCallable() {

						@Override
						public double call() throws Exception {
							return compute(valueLeft, funcRight.call(), funcLocale.call(), funcTimeZone.call());
						}
					};
				}
			} else if (!left.isConstant() && right.isConstant()) {
				// branch with constant locale and time zone data, probably both are constant or
				// both are not
				if (locale.isConstant() && timeZone.isConstant()) {
					return new DoubleCallable() {

						@Override
						public double call() throws Exception {
							return compute(funcLeft.call(), valueRight, valueLocale, valueTimezone);
						}
					};
				} else {
					return new DoubleCallable() {

						@Override
						public double call() throws Exception {
							return compute(funcLeft.call(), valueRight, funcLocale.call(), funcTimeZone.call());
						}
					};
				}
				// both dates are variable
			} else {
				// branch with constant locale and time zone data, probably both are constant or
				// both are not
				if (locale.isConstant() && timeZone.isConstant()) {
					return new DoubleCallable() {

						@Override
						public double call() throws Exception {
							return compute(funcLeft.call(), funcRight.call(), valueLocale, valueTimezone);
						}
					};
				} else {
					return new DoubleCallable() {

						@Override
						public double call() throws Exception {
							return compute(funcLeft.call(), funcRight.call(), funcLocale.call(), funcTimeZone.call());
						}
					};
				}
			}
		} catch (ExpressionParsingException e) {
			throw e;
		} catch (Exception e) {
			throw new ExpressionParsingException(e);
		}
	}

	/**
	 * Computes the result for two input date values with additional optional locale and time zone
	 * arguments.
	 *
	 * @param left
	 *            first date
	 * @param right
	 *            second date
	 * @param valueLocale
	 *            locale string, can be null
	 * @param valueTimezone
	 *            time zone string, can be null
	 * @return the result of the computation.
	 */
	protected abstract double compute(Date left, Date right, String valueLocale, String valueTimezone);

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {

		if (inputTypes.length != 2 && inputTypes.length != 4) {
			throw new FunctionInputException("expression_parser.function_wrong_input_two", getFunctionName(), "2", "4",
					inputTypes.length);
		}
		ExpressionType firstType = inputTypes[0];
		ExpressionType secondType = inputTypes[1];
		if (firstType != ExpressionType.DATE || secondType != ExpressionType.DATE) {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "date");
		}
		if (inputTypes.length == 4) {
			if (inputTypes[2] != ExpressionType.STRING) {
				throw new FunctionInputException("expression_parser.function_wrong_type_at", getFunctionName(), "string",
						"third");
			}
			if (inputTypes[3] != ExpressionType.STRING) {
				throw new FunctionInputException("expression_parser.function_wrong_type_at", getFunctionName(), "string",
						"fourth");
			}
		}
		return ExpressionType.INTEGER;

	}

}
