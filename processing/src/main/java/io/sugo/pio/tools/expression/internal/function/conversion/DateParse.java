package io.sugo.pio.tools.expression.internal.function.conversion;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Callable;

/**
 *
 * A {@link Function} parsing a string or a number to a date.
 *
 * @author Marcel Seifert
 *
 */
public class DateParse extends AbstractFunction {

	/**
	 * Constructs an AbstractFunction with {@link FunctionDescription} generated from the arguments
	 * and the function name generated from the description.
	 *
	 */
	public DateParse() {
		super("conversion.date_parse", 1, Ontology.DATE);
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {
		if (inputEvaluators.length != 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 1,
					inputEvaluators.length);
		}
		ExpressionType type = getResultType(inputEvaluators);

		ExpressionEvaluator input = inputEvaluators[0];

		return new SimpleExpressionEvaluator(type, makeDateCallable(input), isResultConstant(inputEvaluators));
	}

	/**
	 * Builds a Date Callable from one String or one double input argument
	 *
	 * @param inputEvaluator
	 *            the input
	 * @return the resulting callable<String>
	 */
	protected Callable<Date> makeDateCallable(final ExpressionEvaluator inputEvaluator) {

		final Callable<String> funcString = inputEvaluator.getType() == ExpressionType.STRING ? inputEvaluator
				.getStringFunction() : null;

		final DoubleCallable funcDouble = inputEvaluator.getType() != ExpressionType.STRING ? inputEvaluator
						.getDoubleFunction() : null;

		try {
			if (funcString != null) {
				if (inputEvaluator.isConstant()) {
					final Date result = compute(funcString.call());
					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return result;
						}
					};
				} else {
					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return compute(funcString.call());
						}
					};
				}
			} else {
				if (inputEvaluator.isConstant()) {
					final Date result = compute(funcDouble.call());
					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return result;
						}
					};
				} else {
					return new Callable<Date>() {

						@Override
						public Date call() throws Exception {
							return compute(funcDouble.call());
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
	 * Computes the result for one input double value.
	 *
	 * @param value
	 *            the input timestamp
	 *
	 * @return the result of the computation.
	 */
	protected Date compute(double value) {

		if (Double.isNaN(value)) {
			return null;
		}

		long dateLong = (long) value;
		Date date = new Date(dateLong);
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.getTime();
	}

	/**
	 * Computes the result for one input string value.
	 *
	 * @param value
	 *            the input date string
	 *
	 * @return the result of the computation
	 */
	protected Date compute(String dateString) {
		if (dateString == null) {
			return null;
		}
		Date date;
		try {
			// clone because getDateInstance uses an internal pool which can return the
			// same instance for multiple threads
			date = ((DateFormat) DateFormat.getDateInstance(DateFormat.SHORT).clone()).parse(dateString);
			Calendar cal = Calendar.getInstance();
			cal.setTime(date);
			return date;
		} catch (ParseException e) {
			throw new FunctionInputException("expression_parser.invalid_argument.date", getFunctionName(), dateString);
		}
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		ExpressionType input = inputTypes[0];
		if (input == ExpressionType.STRING || input == ExpressionType.DOUBLE || input == ExpressionType.INTEGER) {
			return ExpressionType.DATE;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type_two", getFunctionName(), "nominal",
					"numerical");
		}
	}

}
