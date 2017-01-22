package io.sugo.pio.tools.expression.internal.function.text;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.util.concurrent.Callable;

/**
 * A {@link AbstractFunction} which allows a nominal value and a integer as input and delivers a
 * string as output.
 *
 * @author Thilo Kamradt
 *
 */
public abstract class AbstractStringIntegerInputStringOutputFunction extends AbstractFunction {

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
	public AbstractStringIntegerInputStringOutputFunction(String i18nKey, int numberOfArgumentsToCheck) {
		super(i18nKey, numberOfArgumentsToCheck, Ontology.NOMINAL);
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
	protected Callable<String> makeStringCallable(ExpressionEvaluator left, ExpressionEvaluator right) {
		final Callable<String> funcLeft = left.getStringFunction();
		final DoubleCallable funcRight = right.getDoubleFunction();
		try {

			final String valueLeft = left.isConstant() ? funcLeft.call() : "";
			final double valueRight = right.isConstant() ? funcRight.call() : Double.NaN;

			if (left.isConstant() && right.isConstant()) {
				final String result = compute(valueLeft, valueRight);

				return new Callable<String>() {

					@Override
					public String call() throws Exception {
						return result;
					}

				};
			} else if (left.isConstant()) {
				return new Callable<String>() {

					@Override
					public String call() throws Exception {
						return compute(valueLeft, funcRight.call());
					}

				};

			} else if (right.isConstant()) {
				return new Callable<String>() {

					@Override
					public String call() throws Exception {
						return compute(funcLeft.call(), valueRight);
					}
				};

			} else {
				return new Callable<String>() {

					@Override
					public String call() throws Exception {
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
	 * Computes the result.
	 *
	 * @param text
	 * @param index
	 * @return the result of the computation.
	 */
	protected abstract String compute(String text, double index);

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		if (inputTypes[0] == ExpressionType.STRING
				&& (inputTypes[1] == ExpressionType.INTEGER || inputTypes[1] == ExpressionType.DOUBLE)) {
			return ExpressionType.STRING;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(),
					"(nominal, integer)");
		}
	}

}
