package io.sugo.pio.tools.expression.internal.function.rounding;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 * A {@link Function} to calculate the floor of a numerical value.
 *
 * @author Thilo Kamradt
 *
 */
public class Floor extends Abstract1DoubleInputFunction {

	public Floor() {
		super("rounding.floor", Ontology.INTEGER);
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		ExpressionType firstType = inputTypes[0];
		if (firstType == ExpressionType.INTEGER || firstType == ExpressionType.DOUBLE) {
			return ExpressionType.INTEGER;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "numerical");
		}
	}

	@Override
	protected double compute(double value1) {
		return Math.floor(value1);
	}

}
