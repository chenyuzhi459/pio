package io.sugo.pio.tools.expression.internal.function.conversion;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 *
 * A {@link Function} parsing a string to a date with respect to the locale and pattern strings.
 *
 * @author Marcel Seifert
 *
 */
public class DateParseCustom extends AbstractFunction {

	/**
	 * Constructs an AbstractFunction with {@link FunctionDescription} generated from the arguments
	 * and the function name generated from the description.
	 *
	 */
	public DateParseCustom() {
		super("conversion.date_parse_custom", FunctionDescription.UNFIXED_NUMBER_OF_ARGUMENTS, Ontology.DATE);
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {

		if (inputEvaluators.length != 2 && inputEvaluators.length != 3) {
			throw new FunctionInputException("expression_parser.function_wrong_input_two", getFunctionName(), 2, 3,
					inputEvaluators.length);
		}
		ExpressionType type = getResultType(inputEvaluators);

		return new SimpleExpressionEvaluator(type, makeDateCallable(inputEvaluators), isResultConstant(inputEvaluators));
	}

	/**
	 * Builds a Date Callable from the given string arguments
	 *
	 * @param inputEvaluators
	 *            The input date, input pattern and optional the input locale
	 * @return The resulting Callable<Date>
	 */
	protected Callable<Date> makeDateCallable(ExpressionEvaluator... inputEvaluators) {

		final ExpressionEvaluator date = inputEvaluators[0];
		final ExpressionEvaluator pattern = inputEvaluators[1];

		final Callable<String> funcDateString = date.getStringFunction();
		final Callable<String> funcPattern = pattern.getStringFunction();

		try {
			final String valuePattern = pattern.isConstant() ? funcPattern.call() : null;
			final String valueDate = date.isConstant() ? funcDateString.call() : null;

			if (inputEvaluators.length > 2) {
				ExpressionEvaluator locale = inputEvaluators[2];
				final Callable<String> funcLocale = locale.getStringFunction();
				final String valueLocale = locale.isConstant() ? funcLocale.call() : null;

				if (pattern.isConstant()) {
					if (date.isConstant() && locale.isConstant()) {
						return new Callable<Date>() {

							@Override
							public Date call() throws Exception {
								return compute(valueDate, valuePattern, valueLocale);
							}
						};
					} else if (date.isConstant()) {
						return new Callable<Date>() {

							@Override
							public Date call() throws Exception {
								return compute(valueDate, valuePattern, funcLocale.call());
							}
						};
					} else if (locale.isConstant()) {
						return new Callable<Date>() {

							@Override
							public Date call() throws Exception {
								return compute(funcDateString.call(), valuePattern, valueLocale);
							}
						};
					} else {
						return new Callable<Date>() {

							@Override
							public Date call() throws Exception {
								return compute(funcDateString.call(), valuePattern, funcLocale.call());
							}
						};
					}
				} else {
					if (date.isConstant() && locale.isConstant()) {
						return new Callable<Date>() {

							@Override
							public Date call() throws Exception {
								return compute(valueDate, funcPattern.call(), valueLocale);
							}
						};
					} else if (date.isConstant()) {
						return new Callable<Date>() {

							@Override
							public Date call() throws Exception {
								return compute(valueDate, funcPattern.call(), funcLocale.call());
							}
						};
					} else if (locale.isConstant()) {
						return new Callable<Date>() {

							@Override
							public Date call() throws Exception {
								return compute(funcDateString.call(), funcPattern.call(), valueLocale);
							}
						};
					} else {
						return new Callable<Date>() {

							@Override
							public Date call() throws Exception {
								return compute(funcDateString.call(), funcPattern.call(), funcLocale.call());
							}
						};
					}
				}
			} else {
				if (date.isConstant() && pattern.isConstant()) {

					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return compute(valueDate, valuePattern);
						}
					};
				} else if (date.isConstant()) {
					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return compute(valueDate, funcPattern.call());
						}
					};
				} else if (pattern.isConstant()) {
					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return compute(funcDateString.call(), valuePattern);
						}
					};
				} else {
					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return compute(funcDateString.call(), funcPattern.call());
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
	 * Computes the result for two string input values.
	 *
	 * @param dateString
	 *            the input date string
	 * @param patternString
	 *            the input pattern string
	 * @return the result of the computation.
	 */
	protected Date compute(String dateString, String patternString) {

		String defaultLocale = Locale.getDefault().getISO3Language();
		return compute(dateString, patternString, defaultLocale);
	}

	/**
	 * Computes the result for three string input values.
	 *
	 * @param dateString
	 *            the input date string
	 * @param patternString
	 *            the input pattern string
	 * @param localeString
	 *            the input locale string
	 * @return the result of the computation.
	 */
	protected Date compute(String dateString, String patternString, String localeString) {
		if (dateString == null || patternString == null || localeString == null) {
			return null;
		}
		Locale locale = new Locale(localeString);
		SimpleDateFormat simpleDateFormatter = null;
		Date parsedDate = null;
		try {
			simpleDateFormatter = new SimpleDateFormat(patternString, locale);
			parsedDate = simpleDateFormatter.parse(dateString);
		} catch (IllegalArgumentException e) {
			throw new FunctionInputException("invalid_argument.custom_format", getFunctionName());
		} catch (java.text.ParseException e) {
			throw new FunctionInputException("invalid_argument.date", getFunctionName());
		}
		Calendar cal = Calendar.getInstance(locale);
		cal.setTime(parsedDate);
		return cal.getTime();
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		ExpressionType date = inputTypes[0];
		ExpressionType pattern = inputTypes[1];

		if (date != ExpressionType.STRING) {
			throw new FunctionInputException("expression_parser.function_wrong_type.argument", 1, getFunctionName(),
					"nominal");
		} else if (pattern != ExpressionType.STRING) {
			throw new FunctionInputException("expression_parser.function_wrong_type.argument", 2, getFunctionName(),
					"nominal");
		} else if (inputTypes.length > 2 && inputTypes[2] != ExpressionType.STRING) {
			throw new FunctionInputException("expression_parser.function_wrong_type.argument", 3, getFunctionName(),
					"nominal");
		}

		return ExpressionType.DATE;
	}

}
