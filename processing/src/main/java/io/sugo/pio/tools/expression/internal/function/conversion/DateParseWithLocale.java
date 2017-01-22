package io.sugo.pio.tools.expression.internal.function.conversion;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.Callable;

/**
 *
 * A {@link Function} parsing a string or a number to a date with respect to the locale string.
 *
 * @author Marcel Seifert
 *
 */
public class DateParseWithLocale extends AbstractFunction {

	/**
	 * Constructs an AbstractFunction with {@link FunctionDescription} generated from the arguments
	 * and the function name generated from the description.
	 */
	public DateParseWithLocale() {
		super("conversion.date_parse_loc", 2, Ontology.DATE);
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {
		if (inputEvaluators.length != 2) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 2,
					inputEvaluators.length);
		}
		ExpressionType type = getResultType(inputEvaluators);

		ExpressionEvaluator date = inputEvaluators[0];
		ExpressionEvaluator locale = inputEvaluators[1];

		return new SimpleExpressionEvaluator(type, makeDateCallable(date, locale), isResultConstant(inputEvaluators));
	}

	/**
	 * Builds a Date Callable from one String or double input argument and one String argument
	 *
	 * @param date
	 *            the input date
	 * @param locale
	 *            the input locale
	 *
	 * @return the resulting callable<String>
	 */
	protected Callable<Date> makeDateCallable(final ExpressionEvaluator date, final ExpressionEvaluator locale) {

		final Callable<String> funcDateString = date.getType() == ExpressionType.STRING ? date.getStringFunction() : null;
		final DoubleCallable funcDateDouble = date.getType() != ExpressionType.STRING ? date.getDoubleFunction() : null;
		final Callable<String> funcLocale = locale.getStringFunction();

		try {
			final String valueLocale = locale.isConstant() ? funcLocale.call() : null;

			if (funcDateString != null) {
				final String valueDate = date.isConstant() ? funcDateString.call() : null;
				if (date.isConstant() && locale.isConstant()) {
					final Date result = compute(valueDate, valueLocale);
					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return result;
						}
					};
				} else if (date.isConstant()) {
					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return compute(valueDate, funcLocale.call());
						}

					};

				} else if (locale.isConstant()) {
					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return compute(funcDateString.call(), valueLocale);
						}

					};

				} else {
					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return compute(funcDateString.call(), funcLocale.call());
						}
					};
				}
			} else {
				final double valueDate = date.isConstant() ? funcDateDouble.call() : Double.NaN;
				if (date.isConstant() && locale.isConstant()) {
					final Date result = compute(valueDate, valueLocale);
					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return result;
						}
					};
				} else if (date.isConstant()) {
					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return compute(valueDate, funcLocale.call());
						}

					};

				} else if (locale.isConstant()) {
					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return compute(funcDateDouble.call(), valueLocale);
						}

					};

				} else {
					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return compute(funcDateDouble.call(), funcLocale.call());
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
	 * Computes the result for a double input value and a locale string.
	 *
	 * @param value
	 *            the input timestamp
	 * @param the
	 *            locale string
	 * @return the result of the computation.
	 */
	protected Date compute(double value, String localeString) {

		if (Double.isNaN(value) || localeString == null) {
			return null;
		}
		Locale locale = new Locale(localeString);
		long dateLong = (long) value;
		Date date = new Date(dateLong);
		Calendar cal = Calendar.getInstance(locale);
		cal.setTime(date);
		return cal.getTime();
	}

	/**
	 * Computes the result for a string input value and a locale string.
	 *
	 * @param dateString
	 *            the date string
	 * @param localeString
	 *            the locale string
	 * @return the result of the computation.
	 */
	protected Date compute(String dateString, String localeString) {

		if (dateString == null || localeString == null) {
			return null;
		}
		Locale locale = new Locale(localeString);
		Date date;
		try {
			// clone because getDateInstance uses an internal pool which can return the
			// same instance for multiple threads
			date = ((DateFormat) DateFormat.getDateInstance(DateFormat.SHORT, locale).clone()).parse(dateString);
			Calendar cal = Calendar.getInstance(locale);
			cal.setTime(date);
			return cal.getTime();
		} catch (ParseException e) {
			throw new FunctionInputException("invalid_argument.date", getFunctionName());
		}
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		ExpressionType date = inputTypes[0];
		ExpressionType locale = inputTypes[1];
		if (!(date == ExpressionType.STRING || date == ExpressionType.DOUBLE || date == ExpressionType.INTEGER)) {
			throw new FunctionInputException("expression_parser.function_wrong_type.argument_two", 1, getFunctionName(),
					"nominal", "numerical");
		} else if (locale != ExpressionType.STRING) {
			throw new FunctionInputException("expression_parser.function_wrong_type.argument", 2, getFunctionName(),
					"nominal");
		} else {
			return ExpressionType.DATE;
		}
	}

}
