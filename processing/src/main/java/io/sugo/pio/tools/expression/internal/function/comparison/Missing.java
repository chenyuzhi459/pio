package io.sugo.pio.tools.expression.internal.function.comparison;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.*;
import io.sugo.pio.tools.expression.internal.SimpleExpressionEvaluator;
import io.sugo.pio.tools.expression.internal.function.AbstractFunction;

import java.util.Date;
import java.util.concurrent.Callable;

/**
 * Class for the MISSING function that has 1 arbitrary input
 *
 * @author Sabrina Kirstein
 */
public class Missing extends AbstractFunction {

	/**
	 * Constructs a MISSING Function with 1 parameter with {@link FunctionDescription}
	 */
	public Missing() {
		super("comparison.missing", 1, Ontology.BINOMINAL);
	}

	@Override
	public ExpressionEvaluator compute(ExpressionEvaluator... inputEvaluators) {

		if (inputEvaluators.length != 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), "1",
					inputEvaluators.length);
		}

		ExpressionType type = getResultType(inputEvaluators);
		ExpressionEvaluator evaluator = inputEvaluators[0];

		return new SimpleExpressionEvaluator(makeBooleanCallable(evaluator), isResultConstant(inputEvaluators), type);
	}

	/**
	 * Builds a boolean callable from a {@link ExpressionEvaluator}, where constant results are
	 * evaluated.
	 *
	 * @param evaluator
	 * @return the resulting boolean callable
	 */
	protected Callable<Boolean> makeBooleanCallable(ExpressionEvaluator evaluator) {
		try {
			// act depending on the type of the given evaluator
			switch (evaluator.getType()) {
				case INTEGER:
				case DOUBLE:
					final DoubleCallable funcDouble = evaluator.getDoubleFunction();
					final double valueDouble = evaluator.isConstant() ? funcDouble.call() : Double.NaN;
					if (evaluator.isConstant()) {
						final Boolean result = compute(valueDouble);
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
								return compute(funcDouble.call());
							}
						};
					}
				case STRING:
					final Callable<String> funcString = evaluator.getStringFunction();
					final String valueString = evaluator.isConstant() ? funcString.call() : null;
					if (evaluator.isConstant()) {
						final Boolean result = compute(valueString);
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
								return compute(funcString.call());
							}
						};
					}
				case DATE:
					final Callable<Date> funcDate = evaluator.getDateFunction();
					final Date valueDate = evaluator.isConstant() ? funcDate.call() : null;
					if (evaluator.isConstant()) {
						final Boolean result = compute(valueDate);
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
								return compute(funcDate.call());
							}
						};
					}
				case BOOLEAN:
					final Callable<Boolean> funcBoolean = evaluator.getBooleanFunction();
					final Boolean valueBoolean = evaluator.isConstant() ? funcBoolean.call() : null;
					if (evaluator.isConstant()) {
						final Boolean result = compute(valueBoolean);
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
								return compute(funcBoolean.call());
							}
						};
					}
				default:
					return null;
			}

		} catch (ExpressionParsingException e) {
			throw e;
		} catch (Exception e) {
			throw new ExpressionParsingException(e);
		}
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {

		// has to be one argument
		if (inputTypes.length != 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), "1",
					inputTypes.length);
		}
		// result is always boolean
		return ExpressionType.BOOLEAN;
	}

	/**
	 * Computes the result for a double value.
	 *
	 * @param value
	 * @return the result of the computation.
	 */
	protected Boolean compute(double value) {
		if (Double.isNaN(value)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Computes the result for a boolean value.
	 *
	 * @param value
	 * @return the result of the computation.
	 */
	protected Boolean compute(Boolean value) {
		if (value == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Computes the result for a String value.
	 *
	 * @param value
	 * @return the result of the computation.
	 */
	protected Boolean compute(String value) {
		if (value == null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Computes the result for a Date value.
	 *
	 * @param value
	 * @return the result of the computation.
	 */
	protected Boolean compute(Date value) {
		if (value == null) {
			return true;
		} else {
			return false;
		}
	}
}
