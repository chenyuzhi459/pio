package io.sugo.pio.tools.expression.internal.function.logical;


import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;

import java.util.concurrent.Callable;

/**
 * Abstract class for a function that has 1 logical (numerical, true or false) input
 *
 * @author Sabrina Kirstein
 *
 */
public abstract class AbstractLogicalFunctionWith1Input extends AbstractLogicalFunction {

	/**
	 * Constructs a logical AbstractFunction with 1 parameter with {@link FunctionDescription}
	 * generated from the arguments and the function name generated from the description.
	 *
	 * @param i18nKey
	 *            the key for the {@link FunctionDescription}. The functionName is read from
	 *            "gui.dialog.function.i18nKey.name", the helpTextName from ".help", the groupName
	 *            from ".group", the description from ".description" and the function with
	 *            parameters from ".parameters". If ".parameters" is not present, the ".name" is
	 *            taken for the function with parameters.
	 */
	public AbstractLogicalFunctionWith1Input(String i18nKey) {
		super(i18nKey, 1);
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {

		if (inputEvaluators.length != 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 1,
					inputEvaluators.length);
		}
		ExpressionType type = getResultType(inputEvaluators);
		ExpressionEvaluator evaluator = inputEvaluators[0];

		return new SimpleExpressionEvaluator(makeBooleanCallable(evaluator), isResultConstant(inputEvaluators), type);
	}

	/**
	 * Builds a boolean callable from evaluator using {@link #compute(double)} or
	 * {@link #compute(boolean)}, where constant child results are evaluated.
	 *
	 * @param evaluator
	 * @return the resulting boolean callable
	 */
	protected Callable<Boolean> makeBooleanCallable(ExpressionEvaluator evaluator) {
		ExpressionType inputType = evaluator.getType();

		if (inputType.equals(ExpressionType.DOUBLE) || inputType.equals(ExpressionType.INTEGER)) {

			final DoubleCallable func = evaluator.getDoubleFunction();
			try {
				if (evaluator.isConstant()) {

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
		} else if (inputType.equals(ExpressionType.BOOLEAN)) {

			final Callable<Boolean> func = evaluator.getBooleanFunction();
			try {
				if (evaluator.isConstant()) {

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
		} else {
			return null;
		}
	}

	/**
	 * Computes the result for a double value.
	 *
	 * @param value
	 * @return the result of the computation.
	 */
	protected abstract Boolean compute(double value);

	/**
	 * Computes the result for a boolean value.
	 *
	 * @param value
	 * @return the result of the computation.
	 */
	protected abstract Boolean compute(Boolean value);
}
