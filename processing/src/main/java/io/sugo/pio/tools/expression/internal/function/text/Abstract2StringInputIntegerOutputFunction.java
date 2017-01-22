package io.sugo.pio.tools.expression.internal.function.text;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.util.concurrent.Callable;

/**
 *
 * Abstract class for a {@link Function} that has two string arguments and returns an integer
 * argument.
 *
 * @author David Arnu
 *
 */
public abstract class Abstract2StringInputIntegerOutputFunction extends AbstractFunction {

	/**
	 * Constructs an AbstractFunction with {@link FunctionDescription} generated from the arguments
	 * and the function name generated from the description.
	 *
	 * @param i18nKey
	 *            the key for the {@link FunctionDescription}. The functionName is read from
	 *            "gui.dialog.function.i18nKey.name", the helpTextName from ".help", the groupName
	 *            from ".group", the description from ".description" and the function with
	 *            parameters from ".parameters". If ".parameters" is not present, the ".name" is
	 *            taken for the function with parameters.
	 * @param numberOfArgumentsToCheck
	 *            the fixed number of parameters this functions expects or -1
	 */
	public Abstract2StringInputIntegerOutputFunction(String i18n, int numberOfArgumentsToCheck) {
		super(i18n, numberOfArgumentsToCheck, Ontology.INTEGER);
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

		return new SimpleExpressionEvaluator(makeStringCallable(left, right), type, isResultConstant(inputEvaluators));
	}

	/**
	 * Builds a DoubleCallable from left and right using {@link #compute(String, String)}, where
	 * constant child results are evaluated.
	 *
	 * @param left
	 *            the left input
	 * @param right
	 *            the right input
	 * @return the resulting DoubleCallable
	 */
	protected DoubleCallable makeStringCallable(ExpressionEvaluator left, ExpressionEvaluator right) {
		final Callable<String> funcLeft = left.getStringFunction();
		final Callable<String> funcRight = right.getStringFunction();
		try {

			final String valueLeft = left.isConstant() ? funcLeft.call() : "";
			final String valueRight = right.isConstant() ? funcRight.call() : "";

			if (left.isConstant() && right.isConstant()) {
				final double result = compute(valueLeft, valueRight);

				return new DoubleCallable() {

					@Override
					public double call() throws Exception {
						return result;
					}

				};
			} else if (left.isConstant()) {
				return new DoubleCallable() {

					@Override
					public double call() throws Exception {
						return compute(valueLeft, funcRight.call());
					}

				};

			} else if (right.isConstant()) {
				return new DoubleCallable() {

					@Override
					public double call() throws Exception {
						return compute(funcLeft.call(), valueRight);
					}
				};

			} else {
				return new DoubleCallable() {

					@Override
					public double call() throws Exception {
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
	 * Computes the result for two input String values.
	 *
	 * @param value1
	 * @param value2
	 * @return the result of the computation.
	 */
	protected abstract double compute(String value1, String value2);

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		ExpressionType left = inputTypes[0];
		ExpressionType right = inputTypes[1];
		if (left == ExpressionType.STRING && right == ExpressionType.STRING) {
			return ExpressionType.INTEGER;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "nominal");
		}
	}

}
