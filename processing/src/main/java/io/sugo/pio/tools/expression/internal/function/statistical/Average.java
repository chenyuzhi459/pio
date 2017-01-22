package io.sugo.pio.tools.expression.internal.function.statistical;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.ExpressionType;
import io.sugo.pio.tools.expression.FunctionDescription;
import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.AbstractArbitraryDoubleInputFunction;

/**
 * A {@link Function} for average.
 *
 * @author David Arnu
 *
 */
public class Average extends AbstractArbitraryDoubleInputFunction {

	/**
	 * Constructs an average function.
	 */
	public Average() {
		super("statistical.avg", FunctionDescription.UNFIXED_NUMBER_OF_ARGUMENTS, Ontology.NUMERICAL);

	}

	@Override
	protected ExpressionType computeType(ExpressionType... inputTypes) {

		for (ExpressionType input : inputTypes) {
			if (input != ExpressionType.INTEGER && input != ExpressionType.DOUBLE) {
				throw new FunctionInputException("expression_parser.function_wrong_type", getFunctionName(), "numerical");
			}
		}
		return ExpressionType.DOUBLE;
	}

	@Override
	public double compute(double... values) {
		int n = values.length;
		double avg = 0;

		for (double val : values) {
			avg += val;
		}

		return avg / n;
	}

}
