package io.sugo.pio.tools.expression.internal.function.trigonometric;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.Abstract2DoubleInputFunction;

/**
 *
 * A {@link Function} computing the trigonometric arc tangent of two values.
 *
 * @author Denis Schernov
 *
 */
public class ArcTangent2 extends Abstract2DoubleInputFunction {

	public ArcTangent2() {
		super("trigonometrical.atan2", 2, Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value1, double value2) {
		return Double.isNaN(value1) || Double.isNaN(value2) ? Double.NaN : Math.atan2(value1, value2);
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		ExpressionType left = inputTypes[0];
		ExpressionType right = inputTypes[1];
		if ((left == ExpressionType.INTEGER || left == ExpressionType.DOUBLE)
				&& (right == ExpressionType.INTEGER || right == ExpressionType.DOUBLE)) {
			return ExpressionType.DOUBLE;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "numerical");
		}
	}
}
