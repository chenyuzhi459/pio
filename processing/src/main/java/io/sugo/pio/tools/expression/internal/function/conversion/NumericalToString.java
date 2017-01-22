package io.sugo.pio.tools.expression.internal.function.conversion;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.Tools;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.util.concurrent.Callable;

/**
 *
 * A {@link Function} parsing a number to a string.
 *
 * @author Marcel Seifert
 *
 */
public class NumericalToString extends AbstractFunction {

	/**
	 * Constructs an AbstractFunction with {@link FunctionDescription} generated from the arguments
	 * and the function name generated from the description.
	 */
	public NumericalToString() {
		super("conversion.str", 1, Ontology.NOMINAL);
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {
		if (inputEvaluators.length != 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 1,
					inputEvaluators.length);
		}
		ExpressionType type = getResultType(inputEvaluators);

		ExpressionEvaluator input = inputEvaluators[0];

		return new SimpleExpressionEvaluator(makeStringCallable(input), type, isResultConstant(inputEvaluators));
	}

	/**
	 * Builds a String Callable from a double input argument
	 *
	 * @param inputEvaluator
	 *            the input
	 * @return the resulting callable<String>
	 */
	protected Callable<String> makeStringCallable(final ExpressionEvaluator inputEvaluator) {
		final DoubleCallable func = inputEvaluator.getDoubleFunction();

		try {
			if (inputEvaluator.isConstant()) {
				final String result = compute(func.call());
				return new Callable<String>() {

					@Override
					public String call() throws Exception {
						return result;
					}
				};
			} else {
				return new Callable<String>() {

					@Override
					public String call() throws Exception {
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
	 * Computes the result for one input double value.
	 *
	 * @param value
	 *            the double value to parse
	 *
	 * @return the result of the computation.
	 */
	protected String compute(double value) {
		if (Double.isNaN(value)) {
			return null;
		}
		return Tools.formatIntegerIfPossible(value);
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		ExpressionType input = inputTypes[0];
		if (input == ExpressionType.INTEGER || input == ExpressionType.DOUBLE) {
			return ExpressionType.STRING;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "numerical");
		}
	}

}
