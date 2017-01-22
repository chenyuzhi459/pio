package io.sugo.pio.tools.expression.internal.function.rounding;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.FunctionInputException;

/**
 * A {@link Function} to rint numbers.
 *
 * @author Thilo Kamradt
 *
 */
public class Rint extends Abstract1or2DoubleInputFunction {

	public Rint() {
		super("rounding.rint", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		return Math.rint(value);
	}

	@Override
	protected double compute(double value1, double value2) {
		if (Double.isNaN(value2)) {
			return compute(value1);
		}
		if (Double.isNaN(value1) || value2 == Double.POSITIVE_INFINITY || value2 == Double.NEGATIVE_INFINITY) {
			return Double.NaN;
		}

		int factor = (int) Math.pow(10, (int) value2);
		return Math.rint(value1 * factor) / factor;
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		if (inputTypes[0] == ExpressionType.INTEGER || inputTypes[0] == ExpressionType.DOUBLE && inputTypes.length == 1) {
			return ExpressionType.INTEGER;
		} else if (inputTypes[0] == ExpressionType.DOUBLE && inputTypes.length == 2
				&& (inputTypes[1] == ExpressionType.INTEGER || inputTypes[1] == ExpressionType.DOUBLE)) {
			return ExpressionType.DOUBLE;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "numerical");
		}
	}
}
