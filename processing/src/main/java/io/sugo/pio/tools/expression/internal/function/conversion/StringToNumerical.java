package io.sugo.pio.tools.expression.internal.function.conversion;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.Tools;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.util.concurrent.Callable;

/**
 *
 * A {@link Function} parsing a string to a number.
 *
 * @author Marcel Seifert
 *
 */
public class StringToNumerical extends AbstractFunction {

	/**
	 * {@link NumericalToString} converts infinitys to a symbol, determined by
	 * {@link Tools.FORMAT_SYMBOLS}. Use those strings to allow to recognize them when converting
	 * back.
	 */
	private static final String POSITIVE_INFINITY_STRING = Tools.FORMAT_SYMBOLS.getInfinity();
	private static final String NEGATIVE_INFINITY_STRING = Tools.FORMAT_SYMBOLS.getMinusSign()
			+ Tools.FORMAT_SYMBOLS.getInfinity();

	/**
	 * Constructs an AbstractFunction with {@link FunctionDescription} generated from the arguments
	 * and the function name generated from the description.
	 */
	public StringToNumerical() {
		super("conversion.parse", 1, Ontology.NUMERICAL);
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {
		if (inputEvaluators.length != 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 1,
					inputEvaluators.length);
		}
		ExpressionType type = getResultType(inputEvaluators);

		ExpressionEvaluator input = inputEvaluators[0];

		return new SimpleExpressionEvaluator(makeDoubleCallable(input), type, isResultConstant(inputEvaluators));
	}

	/**
	 * Builds a DoubleCallable from one String input argument
	 *
	 * @param inputEvaluator
	 *            the input
	 * @return the resulting callable<String>
	 */
	protected DoubleCallable makeDoubleCallable(final ExpressionEvaluator inputEvaluator) {
		final Callable<String> func = inputEvaluator.getStringFunction();

		try {
			if (inputEvaluator.isConstant()) {
				final double result = compute(func.call());
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
	 * Computes the result for one input String value.
	 *
	 * @param value
	 *            the string to parse
	 *
	 * @return the result of the computation.
	 */
	protected double compute(String value) {
		if (value != null) {
			try {
				return Double.parseDouble(value);
			} catch (NumberFormatException e) {
				// check if the value is positive or negative infinity as coming from {@link
				// NumericalToString}
				if (POSITIVE_INFINITY_STRING.equals(value)) {
					return Double.POSITIVE_INFINITY;
				} else if (NEGATIVE_INFINITY_STRING.equals(value)) {
					return Double.NEGATIVE_INFINITY;
				}
				return Double.NaN;
			}
		}
		return Double.NaN;
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		ExpressionType input = inputTypes[0];
		if (input == ExpressionType.STRING) {
			return ExpressionType.DOUBLE;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "nominal");
		}
	}

}
