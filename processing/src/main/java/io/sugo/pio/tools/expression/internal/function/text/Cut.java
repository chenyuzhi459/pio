package io.sugo.pio.tools.expression.internal.function.text;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.util.concurrent.Callable;

/**
 * A {@link Function} to cut a nominal value.
 *
 * @author Thilo Kamradt
 *
 */
public class Cut extends AbstractFunction {

	/**
	 * Creates a function to cut a nominal value.
	 */
	public Cut() {
		super("text_transformation.cut", 3, Ontology.NOMINAL);
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {
		if (inputEvaluators.length != 3) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 3,
					inputEvaluators.length);
		}
		ExpressionType type = getResultType(inputEvaluators);

		ExpressionEvaluator text = inputEvaluators[0];
		ExpressionEvaluator startIndex = inputEvaluators[1];
		ExpressionEvaluator length = inputEvaluators[2];

		return new SimpleExpressionEvaluator(makeStringCallable(text, startIndex, length), type,
				isResultConstant(inputEvaluators));
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
	protected Callable<String> makeStringCallable(ExpressionEvaluator text, ExpressionEvaluator startIndex,
												  ExpressionEvaluator length) {

		final Callable<String> funcText = text.getStringFunction();
		final DoubleCallable funcIndex = startIndex.getDoubleFunction();
		final DoubleCallable funcLength = length.getDoubleFunction();
		try {

			final String valueText = text.isConstant() ? funcText.call() : "";
			final double valueIndex = startIndex.isConstant() ? funcIndex.call() : Double.NaN;
			final double valueLength = length.isConstant() ? funcLength.call() : Double.NaN;

			// all three expressions are const
			if (text.isConstant() && startIndex.isConstant() && length.isConstant()) {
				final String result = compute(valueText, valueIndex, valueLength);

				return new Callable<String>() {

					@Override
					public String call() throws Exception {
						return result;
					}

				};
				// two const expressions
			} else if (!text.isConstant() && startIndex.isConstant() && length.isConstant()) {
				return new Callable<String>() {

					@Override
					public String call() throws Exception {
						return compute(funcText.call(), valueIndex, valueLength);
					}

				};
			} else if (text.isConstant() && !startIndex.isConstant() && length.isConstant()) {
				return new Callable<String>() {

					@Override
					public String call() throws Exception {
						return compute(valueText, funcIndex.call(), valueLength);
					}

				};
			} else if (text.isConstant() && startIndex.isConstant() && !length.isConstant()) {
				return new Callable<String>() {

					@Override
					public String call() throws Exception {
						return compute(valueText, valueIndex, funcLength.call());
					}

				};
				// only one expression is const
			} else if (text.isConstant() && !startIndex.isConstant() && !length.isConstant()) {
				return new Callable<String>() {

					@Override
					public String call() throws Exception {
						return compute(valueText, funcIndex.call(), funcLength.call());
					}

				};
			} else if (!text.isConstant() && startIndex.isConstant() && !length.isConstant()) {
				return new Callable<String>() {

					@Override
					public String call() throws Exception {
						return compute(funcText.call(), valueIndex, funcLength.call());
					}

				};
			} else if (!text.isConstant() && !startIndex.isConstant() && length.isConstant()) {
				return new Callable<String>() {

					@Override
					public String call() throws Exception {
						return compute(funcText.call(), funcIndex.call(), valueLength);
					}

				};
			} else {
				// no expression is const
				return new Callable<String>() {

					@Override
					public String call() throws Exception {
						return compute(funcText.call(), funcIndex.call(), funcLength.call());
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
	 * @param length
	 * @return the result of the computation.
	 */
	protected String compute(String text, double index, double length) {
		if (Double.isNaN(index)) {
			// this must be changed for compatibility with the old parser
			index = 0;
		}
		if (Double.isNaN(length)) {
			// this must be changed for compatibility with the old parser
			length = 0;
		}
		if (text == null) {
			return null;
		} else if (index < 0 || length < 0) {
			throw new FunctionInputException("expression_parser.function_non_negative", getFunctionName());
		} else if (index + length > text.length()) {
			throw new FunctionInputException("expression_parser.parameter_value_too_big", "start", "length",
					getFunctionName(), text);
		}
		return text.substring((int) index, (int) index + (int) length);
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		if (inputTypes[0] == ExpressionType.STRING
				&& (inputTypes[1] == ExpressionType.INTEGER || inputTypes[1] == ExpressionType.DOUBLE)
				&& (inputTypes[2] == ExpressionType.INTEGER || inputTypes[2] == ExpressionType.DOUBLE)) {
			return ExpressionType.STRING;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(),
					"(nominal,integer,integer)");
		}
	}
}
