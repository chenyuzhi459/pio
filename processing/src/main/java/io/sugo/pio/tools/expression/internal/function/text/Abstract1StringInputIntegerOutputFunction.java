package io.sugo.pio.tools.expression.internal.function.text;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.util.concurrent.Callable;

/**
 * A {@link AbstractFunction} which allow exactly one nominal value as input and has an integer as
 * output.
 *
 * @author Thilo Kamradt
 *
 */
public abstract class Abstract1StringInputIntegerOutputFunction extends AbstractFunction {

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
	 */
	public Abstract1StringInputIntegerOutputFunction(String i18n) {
		super(i18n, 1, Ontology.INTEGER);
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {
		if (inputEvaluators.length != 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 1,
					inputEvaluators.length);
		}
		ExpressionType type = getResultType(inputEvaluators);

		ExpressionEvaluator eEvaluator = inputEvaluators[0];

		return new SimpleExpressionEvaluator(makeStringCallable(eEvaluator), type, isResultConstant(inputEvaluators));
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
	protected DoubleCallable makeStringCallable(ExpressionEvaluator evaluator) {
		final Callable<String> funcEvaluator = evaluator.getStringFunction();
		try {

			final String valueLeft = evaluator.isConstant() ? funcEvaluator.call() : "";

			if (evaluator.isConstant()) {
				final double result = compute(valueLeft);

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
						return compute(funcEvaluator.call());
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
	 * @param value1
	 * @return the result of the computation.
	 */
	protected abstract double compute(String value1);

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		if (inputTypes[0] == ExpressionType.STRING) {
			return ExpressionType.INTEGER;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "nominal");
		}
	}
}
