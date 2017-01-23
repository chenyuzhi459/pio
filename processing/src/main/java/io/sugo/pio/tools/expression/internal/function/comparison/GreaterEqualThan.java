package io.sugo.pio.tools.expression.internal.function.comparison;



/**
 * Class for the GREATER OR EQUAL THAN function that has two numerical or two nominal inputs
 *
 * @author Sabrina Kirstein
 */
public class GreaterEqualThan extends AbstractComparisonFunctionWith2Inputs {

	/**
	 * Constructs a GREATER OR EQUAL THAN Function with 2 parameters with
	 * {@link FunctionDescription}
	 */
	public GreaterEqualThan() {
		super("comparison.greater_equals");
	}

	@Override
	protected Boolean compute(double left, double right) {
		// false for Double.NaN values
		return left >= right;
	}

	@Override
	protected Boolean compute(String left, String right) {
		if (left == null || right == null) {
			// was an error before, consistent to double compute function
			return false;
		} else {
			return left.compareTo(right) >= 0;
		}
	}
}