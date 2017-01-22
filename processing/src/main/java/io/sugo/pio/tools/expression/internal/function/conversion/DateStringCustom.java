package io.sugo.pio.tools.expression.internal.function.conversion;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 *
 * A {@link Function} parsing a date to a string with respect to the pattern and optional locale.
 *
 * @author Marcel Seifert
 *
 */
public class DateStringCustom extends AbstractFunction {

	/**
	 * Constructs an AbstractFunction with {@link FunctionDescription} generated from the arguments
	 * and the function name generated from the description.
	 */
	public DateStringCustom() {
		super("conversion.date_str_custom", FunctionDescription.UNFIXED_NUMBER_OF_ARGUMENTS, Ontology.NOMINAL);
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {
		if (inputEvaluators.length < 2 || inputEvaluators.length > 3) {
			throw new FunctionInputException("expression_parser.function_wrong_input_two", getFunctionName(), 2, 3,
					inputEvaluators.length);
		}
		ExpressionType type = getResultType(inputEvaluators);

		return new SimpleExpressionEvaluator(makeStringCallable(inputEvaluators), type, isResultConstant(inputEvaluators));
	}

	/**
	 * Builds a String Callable from one date and three string arguments
	 *
	 * @param inputEvaluators
	 *            the input
	 * @return the resulting callable<String>
	 */
	protected Callable<String> makeStringCallable(final ExpressionEvaluator inputEvaluators[]) {

		final ExpressionEvaluator date = inputEvaluators[0];
		final ExpressionEvaluator pattern = inputEvaluators[1];

		final Callable<Date> funcDate = date.getDateFunction();
		final Callable<String> funcPattern = pattern.getStringFunction();

		try {
			final Date valueDate = date.isConstant() ? funcDate.call() : null;
			final String valuePattern = pattern.isConstant() ? funcPattern.call() : null;

			if (inputEvaluators.length > 2) {
				ExpressionEvaluator locale = inputEvaluators[2];
				final Callable<String> funcLocale = locale.getStringFunction();
				final String valueLocale = locale.isConstant() ? funcLocale.call() : null;

				if (locale.isConstant()) {
					if (date.isConstant() && pattern.isConstant()) {
						return new Callable<String>() {

							@Override
							public String call() throws Exception {
								return compute(valueDate, valuePattern, valueLocale);
							}
						};
					} else if (date.isConstant()) {
						return new Callable<String>() {

							@Override
							public String call() throws Exception {
								return compute(valueDate, funcPattern.call(), valueLocale);
							}

						};

					} else if (pattern.isConstant()) {
						return new Callable<String>() {

							@Override
							public String call() throws Exception {
								return compute(funcDate.call(), valuePattern, valueLocale);
							}

						};

					} else {
						return new Callable<String>() {

							@Override
							public String call() throws Exception {
								return compute(funcDate.call(), funcPattern.call(), valueLocale);
							}
						};
					}
				} else {
					if (date.isConstant() && pattern.isConstant()) {

						return new Callable<String>() {

							@Override
							public String call() throws Exception {
								return compute(valueDate, valuePattern, funcLocale.call());
							}
						};
					} else if (date.isConstant()) {
						return new Callable<String>() {

							@Override
							public String call() throws Exception {
								return compute(valueDate, funcPattern.call(), funcLocale.call());
							}

						};

					} else if (pattern.isConstant()) {
						return new Callable<String>() {

							@Override
							public String call() throws Exception {
								return compute(funcDate.call(), valuePattern, funcLocale.call());
							}

						};

					} else {
						return new Callable<String>() {

							@Override
							public String call() throws Exception {
								return compute(funcDate.call(), funcPattern.call(), funcLocale.call());
							}
						};
					}
				}
			} else {
				if (date.isConstant() && pattern.isConstant()) {
					return new Callable<String>() {

						@Override
						public String call() throws Exception {
							return compute(valueDate, valuePattern);
						}
					};
				} else if (date.isConstant()) {
					return new Callable<String>() {

						@Override
						public String call() throws Exception {
							return compute(valueDate, funcPattern.call());
						}

					};

				} else if (pattern.isConstant()) {
					return new Callable<String>() {

						@Override
						public String call() throws Exception {
							return compute(funcDate.call(), valuePattern);
						}

					};

				} else {
					return new Callable<String>() {

						@Override
						public String call() throws Exception {
							return compute(funcDate.call(), funcPattern.call());
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
	 * Computes the result for one date and one string input value.
	 *
	 * @param dateDate
	 *            the input date
	 * @param patternString
	 *            the pattern string
	 * @return the result of the computation.
	 */
	protected String compute(Date dateDate, String patternString) {
		String defaultLocale = Locale.getDefault().getISO3Language();

		return compute(dateDate, patternString, defaultLocale);
	}

	/**
	 * Computes the result for one date and two string input values.
	 *
	 * @param dateDate
	 *            the input date
	 * @param patternString
	 *            the pattern string
	 * @param localeString
	 *            the locale string
	 * @return the result of the computation.
	 */
	protected String compute(Date dateDate, String patternString, String localeString) {
		if (dateDate == null || patternString == null || localeString == null) {
			return null;
		}

		Locale locale = new Locale(localeString);

		SimpleDateFormat simpleDateFormatter;
		try {
			simpleDateFormatter = new SimpleDateFormat(patternString, locale);
		} catch (IllegalArgumentException e) {
			throw new FunctionInputException("invalid_argument.custom_format", getFunctionName());
		}

		String result = simpleDateFormatter.format(dateDate);
		return result;
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		ExpressionType date = inputTypes[0];
		ExpressionType pattern = inputTypes[1];
		// inputTypes[2] is locale if inputTypes.length == 3
		if (date != ExpressionType.DATE) {
			throw new FunctionInputException("expression_parser.function_wrong_type.argument", 1, getFunctionName(), "date");
		} else if (pattern != ExpressionType.STRING) {
			throw new FunctionInputException("expression_parser.function_wrong_type.argument", 2, getFunctionName(),
					"string");
		} else if (inputTypes.length > 2 && inputTypes[2] != ExpressionType.STRING) {
			throw new FunctionInputException("expression_parser.function_wrong_type.argument", 3, getFunctionName(),
					"string");
		} else {
			return ExpressionType.STRING;
		}
	}

}
