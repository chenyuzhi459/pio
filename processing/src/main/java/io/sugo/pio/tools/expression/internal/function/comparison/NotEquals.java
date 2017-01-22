package io.sugo.pio.tools.expression.internal.function.comparison;


import java.util.Date;

/**
 * Class for the NOT EQUALS function that has 2 arbitrary inputs
 *
 * @author Sabrina Kirstein
 *
 */
public class NotEquals extends AbstractEqualityFunctionWith2Inputs {

	/**
	 * Constructs a NOT EQUALS Function with 2 parameters with {@link FunctionDescription}
	 */
	public NotEquals() {
		super("comparison.not_equals");
	}

	@Override
	protected Boolean compute(double left, double right) {
		if (Double.isNaN(left) && Double.isNaN(right)) {
			return false;
		}
		if (Double.isNaN(left) || Double.isNaN(right)) {
			return true;
		}
		return left != right;
	}

	@Override
	protected Boolean compute(double left, Boolean right) {
		if (Double.isNaN(left) || right == null) {
			return true;
		}
		double rightValue = right ? 1 : 0;
		return left != rightValue;
	}

	@Override
	protected Boolean compute(Boolean left, Boolean right) {
		if (left == null && right == null) {
			return false;
		} else if (left == null || right == null) {
			return true;
		}
		return left != right;
	}

	@Override
	protected Boolean compute(double left, String right) {
		if (right == null || Double.isNaN(left)) {
			return true;
		}
		try {
			return left != Double.parseDouble(right);
		} catch (Exception e) {
			return true;
		}
	}

	@Override
	protected Boolean compute(double left, Date right) {
		if (right == null || Double.isNaN(left)) {
			return true;
		}
		return right.getTime() != left;
	}

	@Override
	protected Boolean compute(Boolean left, String right) {
		if (left == null || right == null) {
			return true;
		}
		return !(left + "").equals(right);
	}

	@Override
	protected Boolean compute(Boolean left, Date right) {
		return true;
	}

	@Override
	protected Boolean compute(String left, String right) {
		if (left == null && right == null) {
			return false;
		}
		if (left == null || right == null) {
			return true;
		}
		return !left.equals(right);
	}

	@Override
	protected Boolean compute(String left, Date right) {
		return true;
	}

	@Override
	protected Boolean compute(Date left, Date right) {
		if (left == null && right == null) {
			return false;
		}
		if (left == null || right == null) {
			return true;
		}
		return !left.equals(right);
	}
}
