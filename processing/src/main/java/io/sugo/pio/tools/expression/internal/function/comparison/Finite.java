package io.sugo.pio.tools.expression.internal.function.comparison;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.util.concurrent.Callable;

/**
 * A {@link Function} to check whether the numerical argument is finite or not.
 *
 * @author Thilo Kamradt
 *
 */
public class Finite extends AbstractFunction {

	/**
	 * Constructs the isFinite() function with {@link FunctionDescription}.
	 */
	public Finite() {
		super("comparison.finite", 1, Ontology.BINOMINAL);
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {
		if (inputEvaluators.length != 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 1,
					inputEvaluators.length);
		}
		ExpressionType type = getResultType(inputEvaluators);

		ExpressionEvaluator argument = inputEvaluators[0];

		return new SimpleExpressionEvaluator(makeBoolCallable(argument), isResultConstant(inputEvaluators), type);
	}

	/**
	 * Builds a boolean callable from a single input {@link #compute(double)}, where constant child
	 * results are evaluated.
	 *
	 * @param input
	 *            the input
	 * @return the resulting boolean callable
	 */
	private Callable<Boolean> makeBoolCallable(ExpressionEvaluator input) {
		final DoubleCallable func = input.getDoubleFunction();

		try {
			if (input.isConstant()) {
				final Boolean result = compute(func.call());
				return new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return result;
					}
				};
			} else {
				return new Callable<Boolean>() {

					@Override
					public Boolean call() throws Exception {
						return compute(func.call());
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
	 * Checks whether the parameter is finite or not.
	 *
	 * @param value
	 *            the variable to check
	 * @return false if value is infinite else true.
	 */
	protected Boolean compute(double value) {
		if (Double.isNaN(value)) {
			return null;
		}
		return !Double.isInfinite(value);
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		if (inputTypes[0] == ExpressionType.INTEGER || inputTypes[0] == ExpressionType.DOUBLE) {
			return ExpressionType.BOOLEAN;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "numerical");
		}
	}

}
