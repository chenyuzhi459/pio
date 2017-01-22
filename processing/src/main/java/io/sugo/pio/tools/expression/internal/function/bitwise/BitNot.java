package io.sugo.pio.tools.expression.internal.function.bitwise;

import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;


/**
 * A {@link Function} to calculate the complement of the bit representation of an integer.
 *
 * @author Thilo Kamradt
 *
 */
public class BitNot extends Abstract1DoubleInputFunction {

	public BitNot() {
		super("bitwise.bit_not", Ontology.INTEGER);
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		if (inputTypes[0] == ExpressionType.INTEGER) {
			return ExpressionType.INTEGER;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "integer");
		}
	}

	@Override
	protected double compute(double value1) {
		return ~(int) value1;
	}
}
