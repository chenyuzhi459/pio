package io.sugo.pio.tools.expression.internal.function.bitwise;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.Abstract2DoubleInputFunction;

/**
 * A {@link Function} to calculate the 'and' value of the bit representation of two integers.
 *
 * @author Thilo Kamradt
 *
 */
public class BitAnd extends Abstract2DoubleInputFunction {

	public BitAnd() {
		super("bitwise.bit_and", 2, Ontology.INTEGER);
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		if (inputTypes[0] == ExpressionType.INTEGER && inputTypes[1] == ExpressionType.INTEGER) {
			return ExpressionType.INTEGER;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "integer");
		}
	}

	@Override
	protected double compute(double value1, double value2) {
		return (int) value1 & (int) value2;
	}

}
