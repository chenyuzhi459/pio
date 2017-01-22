package io.sugo.pio.tools.expression.internal.function.logical;



/**
 * Class for the AND function that has 2 logical (numerical, true or false) inputs
 *
 * @author Sabrina Kirstein
 *
 */
public class And extends AbstractLogicalFunctionWith2Inputs {

	/**
	 * Constructs a AND Function with 2 parameters with {@link FunctionDescription}
	 */
	public And() {
		super("logical.and");
	}

	@Override
	protected Boolean compute(double left, double right) {
		if (Double.isNaN(left) || Double.isNaN(right)) {
			return null;
		}
		boolean leftValue = Math.abs(left) < Double.MIN_VALUE * 2 ? false : true;
		boolean rightValue = Math.abs(right) < Double.MIN_VALUE * 2 ? false : true;
		return leftValue && rightValue;
	}

	@Override
	protected Boolean compute(double left, Boolean right) {
		if (Double.isNaN(left) || right == null) {
			return null;
		}
		boolean leftValue = Math.abs(left) < Double.MIN_VALUE * 2 ? false : true;
		return leftValue && right;
	}

	@Override
	protected Boolean compute(Boolean left, Boolean right) {
		if (left == null || right == null) {
			return null;
		}
		return left && right;
	}

}
