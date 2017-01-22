package io.sugo.pio.tools.expression.internal.function;


import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;

/**
 * Abstract class for a {@link Function} that has arbitrary many arguments.
 *
 * @author David Arnu
 *
 */
public abstract class AbstractArbitraryDoubleInputFunction extends AbstractFunction {

	public AbstractArbitraryDoubleInputFunction(String i18n, int numberOfArgumentsToCheck, int returnType) {
		super(i18n, numberOfArgumentsToCheck, returnType);
	}

	/**
	 * Builds a double callable from an arbitrary number of inputs
	 *
	 * @param inputEvaluators
	 *            the inputs
	 * @return the resulting double callable
	 *
	 * @author David Arnu
	 */
	protected DoubleCallable makeDoubleCallable(final ExpressionEvaluator[] inputEvaluators) {

		final int inputLength = inputEvaluators.length;
		final double[] constantValues = new double[inputLength];
		try {
			int i = 0;
			for (ExpressionEvaluator exp : inputEvaluators) {
				constantValues[i] = exp.isConstant() ? exp.getDoubleFunction().call() : Double.NaN;
				i++;
			}
			// because we can assume that the function is constant, this is a way to check if all
			// values are constant
			if (isResultConstant(inputEvaluators)) {
				final double result = compute(constantValues);
				return new DoubleCallable() {

					@Override
					public double call() throws Exception {
						return result;
					}
				};

			} else {
				final double[] values = new double[inputLength];

				return new DoubleCallable() {

					@Override
					public double call() throws Exception {
						for (int j = 0; j < inputLength; j++) {
							values[j] = inputEvaluators[j].isConstant() ? constantValues[j] : inputEvaluators[j]
									.getDoubleFunction().call();
						}
						return compute(values);
					}
				};
			}
		} catch (ExpressionParsingException e) {
			throw e;
		} catch (Exception e) {
			throw new ExpressionParsingException(e);
		}

	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {

		boolean allInteger = true;
		for (ExpressionType input : inputTypes) {
			if (input != ExpressionType.INTEGER) {
				allInteger = false;
				if (input != ExpressionType.DOUBLE) {
					throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "numerical");
				}
			}

		}
		if (allInteger) {
			return ExpressionType.INTEGER;
		} else {
			return ExpressionType.DOUBLE;
		}
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {

		if (inputEvaluators.length < 1) {
			throw new FunctionInputException("expression_parser.function_wrong_minimum_input", getFunctionName(), 1,
					inputEvaluators.length);
		}

		ExpressionType type = getResultType(inputEvaluators);

		return new SimpleExpressionEvaluator(makeDoubleCallable(inputEvaluators), type, isResultConstant(inputEvaluators));
	}

	/**
	 * Computes the result of a function with arbitrary many double values as input arguments
	 *
	 * @param values
	 * @return the single value result of the computation
	 */
	protected abstract double compute(double... values);

}
