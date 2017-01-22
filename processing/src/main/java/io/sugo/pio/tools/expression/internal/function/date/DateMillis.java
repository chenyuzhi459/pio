package io.sugo.pio.tools.expression.internal.function.date;

import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.util.Date;
import java.util.concurrent.Callable;


/**
 * A {@link Function} that returns the time in milliseconds .
 *
 * @author David Arnu
 *
 */
public class DateMillis extends AbstractFunction {

	public DateMillis() {
		super("date.date_millis", 1, Ontology.INTEGER);
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {
		if (inputEvaluators.length != 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 1,
					inputEvaluators.length);
		}
		ExpressionType resultType = getResultType(inputEvaluators);
		return new SimpleExpressionEvaluator(makeDoubleCallable(inputEvaluators[0]), resultType,
				isResultConstant(inputEvaluators));
	}

	private DoubleCallable makeDoubleCallable(ExpressionEvaluator date) {

		final Callable<Date> funcDate = date.getDateFunction();

		try {
			final Date valueDate = date.isConstant() ? funcDate.call() : null;

			if (date.isConstant()) {
				final double result = compute(valueDate);
				return new DoubleCallable() {

					@Override
					public double call() throws Exception {
						return result;
					}

				};

			} else {
				return new DoubleCallable() {

					@Override
					public double call() throws Exception {
						return compute(funcDate.call());
					}

				};
			}
		} catch (ExpressionParsingException e) {
			throw e;
		} catch (Exception e) {
			throw new ExpressionParsingException(e);
		}

	}

	private double compute(Date valueDate) {
		if (valueDate == null) {
			return Double.NaN;
		} else {
			return valueDate.getTime();
		}
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {

		if (inputTypes[0] != ExpressionType.DATE) {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "date");
		}
		return ExpressionType.INTEGER;
	}

}
