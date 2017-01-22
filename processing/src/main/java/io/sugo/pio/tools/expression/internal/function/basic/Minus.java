package io.sugo.pio.tools.expression.internal.function.basic;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.Abstract2DoubleInputFunction;

/**
 * A {@link Function} for subtraction.
 *
 * @author Gisa Schaefer
 *
 */
public class Minus extends Abstract2DoubleInputFunction {

	/**
	 * Constructs a subtraction function.
	 */
	public Minus() {
		super("basic.subtraction", FunctionDescription.UNFIXED_NUMBER_OF_ARGUMENTS, Ontology.NUMERICAL);

	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {
		if (inputEvaluators.length != 2 && inputEvaluators.length != 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input_two", getFunctionName(), 1, 2,
					inputEvaluators.length);
		}

		ExpressionType type = getResultType(inputEvaluators);
		DoubleCallable func;

		if (inputEvaluators.length == 1) {
			func = makeDoubleCallable(inputEvaluators[0]);
		} else {
			func = makeDoubleCallable(inputEvaluators[0], inputEvaluators[1]);
		}

		return new SimpleExpressionEvaluator(func, type, isResultConstant(inputEvaluators));
	}

	/**
	 * Creates the callable for one double callable input.
	 *
	 * @param expressionEvaluator
	 * @return
	 */
	private DoubleCallable makeDoubleCallable(ExpressionEvaluator input) {
		final DoubleCallable inputFunction = input.getDoubleFunction();
		try {
			if (input.isConstant()) {
				final double inputValue = inputFunction.call();
				final double returnValue = -inputValue;
				return new DoubleCallable() {

					@Override
					public double call() throws Exception {
						return returnValue;
					}

				};
			} else {
				return new DoubleCallable() {

					@Override
					public double call() throws Exception {
						return -inputFunction.call();
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
		ExpressionType firstType = inputTypes[0];
		if (firstType == ExpressionType.INTEGER && (inputTypes.length == 1 || inputTypes[1] == ExpressionType.INTEGER)) {
			return ExpressionType.INTEGER;
		} else if ((firstType == ExpressionType.INTEGER || firstType == ExpressionType.DOUBLE)
				&& (inputTypes.length == 1 || inputTypes[1] == ExpressionType.INTEGER || inputTypes[1] == ExpressionType.DOUBLE)) {
			return ExpressionType.DOUBLE;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "numerical");
		}
	}

	@Override
	protected double compute(double value1, double value2) {
		return value1 - value2;
	}

}
