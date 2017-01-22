package io.sugo.pio.tools.expression.internal.function.statistical;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.Abstract2DoubleInputFunction;
import org.apache.commons.math3.util.CombinatoricsUtils;

/**
 *
 * A {@link Function} for binominal coefficents.
 *
 * @author David Arnu
 *
 */
public class Binominal extends Abstract2DoubleInputFunction {

	/**
	 * Constructs a binominal function.
	 */
	public Binominal() {
		super("statistical.binom", 2, Ontology.INTEGER);
	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {
		ExpressionType left = inputTypes[0];
		ExpressionType right = inputTypes[1];

		if (left == ExpressionType.INTEGER && right == ExpressionType.INTEGER) {
			return ExpressionType.INTEGER;
		} else {
			throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "integer");
		}
	}

	@Override
	protected double compute(double value1, double value2) {

		// special case for handling missing values
		if (Double.isNaN(value1) || Double.isNaN(value2)) {
			return Double.NaN;
		}

		if (value1 < 0 || value2 < 0) {
			throw new FunctionInputException("expression_parser.function_non_negative", getFunctionName());
		}
		// This is the common definition for the case for k > n.
		if (value2 > value1) {
			return 0;
		} else {
			return CombinatoricsUtils.binomialCoefficientDouble((int) value1, (int) value2);
		}
	}

}
