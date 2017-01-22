package io.sugo.pio.tools.expression.internal.function.mathematical;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 * A {@link Function} computing signum of a number. Returns +1, if the value is zero.
 *
 * @author Marcel Seifert
 *
 */
public class Signum extends Abstract1DoubleInputFunction {

	public Signum() {
		super("mathematical.sgn", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		double sgn = Math.signum(value);
		return sgn;
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		ExpressionType input = inputTypes[0];
		if (input == ExpressionType.INTEGER || input == ExpressionType.DOUBLE) {
			return ExpressionType.INTEGER;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "numerical");
		}
	}

}
