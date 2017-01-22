package io.sugo.pio.tools.expression.internal.function.comparison;



/**
 * Class for the LESS THAN function that has two numerical or two nominal inputs
 *
 * @author Sabrina Kirstein
 */
public class LessThan extends AbstractComparisonFunctionWith2Inputs {

	/**
	 * Constructs a LESS THAN Function with 2 parameters with {@link FunctionDescription}
	 */
	public LessThan() {
		super("comparison.less_than");
	}

	@Override
	protected Boolean compute(double left, double right) {
		if (Double.isNaN(left) || Double.isNaN(right)) {
			// like it was done before
			return false;
		} else {
			return left < right;
		}
	}

	@Override
	protected Boolean compute(String left, String right) {
		if (left == null || right == null) {
			// was an error before, consistent to double compute function
			return false;
		} else {
			return left.compareTo(right) < 0;
		}
	}
}
