package io.sugo.pio.tools.expression.internal.function.date;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.ExpressionEvaluator;
import io.sugo.pio.tools.expression.ExpressionParsingException;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Abstract class for a {@link Function} that has two date arguments and returns a boolean.
 *
 * @author David Arnu
 *
 */
public abstract class Abstract2DateInputBooleanOutput extends AbstractFunction {

	public Abstract2DateInputBooleanOutput(String i18nKey) {
		super(i18nKey, 2, Ontology.BINOMINAL);
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {
		if (inputEvaluators.length != 2) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 2,
					inputEvaluators.length);
		}
		ExpressionType type = getResultType(inputEvaluators);

		ExpressionEvaluator left = inputEvaluators[0];
		ExpressionEvaluator right = inputEvaluators[1];

		return new SimpleExpressionEvaluator(makeBooleanCallable(left, right), isResultConstant(inputEvaluators), type);
	}

	/**
	 * Builds a DoubleCallable from left and right using {@link #compute(Date, Date)}, where
	 * constant child results are evaluated.
	 *
	 * @param left
	 *            the left input
	 * @param right
	 *            the right input
	 * @return the resulting DoubleCallable
	 */
	private Callable<Boolean> makeBooleanCallable(ExpressionEvaluator left, ExpressionEvaluator right) {
		final Callable<Date> funcLeft = left.getDateFunction();
		final Callable<Date> funcRight = right.getDateFunction();
		try {
			final Date valueLeft = left.isConstant() ? funcLeft.call() : null;
			final Date valueRight = right.isConstant() ? funcRight.call() : null;

			if (left.isConstant() && right.isConstant()) {
				final Boolean result = compute(valueLeft, valueRight);

				return new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return result;
					}

				};
			} else if (left.isConstant() && !right.isConstant()) {
				return new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return compute(valueLeft, funcRight.call());
					}

				};

			} else if (!left.isConstant() && right.isConstant()) {
				return new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return compute(funcLeft.call(), valueRight);
					}
				};

			} else {
				return new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return compute(funcLeft.call(), funcRight.call());
					}
				};
			}
		} catch (ExpressionParsingException e) {
			throw e;
		} catch (Exception e) {
			throw new ExpressionParsingException(e);
		}
	}

	/**
	 * Computes the result for two input date values.
	 *
	 * @param left
	 * @param right
	 * @return the result of the computation.
	 */
	protected abstract Boolean compute(Date left, Date right);

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		ExpressionType left = inputTypes[0];
		ExpressionType right = inputTypes[1];
		if (left == ExpressionType.DATE && right == ExpressionType.DATE) {
			return ExpressionType.BOOLEAN;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "date");
		}
	}

}
