package io.sugo.pio.tools.expression.internal.function.mathematical;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 * A {@link Function} computing the absolute value of a number.
 *
 * @author Marcel Seifert
 *
 */
public class AbsoluteValue extends Abstract1DoubleInputFunction {

	public AbsoluteValue() {
		super("mathematical.abs", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		return Math.abs(value);
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		ExpressionType input = inputTypes[0];
		if (input == ExpressionType.DOUBLE) {
			return ExpressionType.DOUBLE;
		} else if (input == ExpressionType.INTEGER) {
			return ExpressionType.INTEGER;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "numerical");
		}
	}
}
