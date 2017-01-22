package io.sugo.pio.tools.expression.internal.function.logical;



/**
 * Class for the NOT function that has 1 logical (numerical, true or false) input
 *
 * @author Sabrina Kirstein
 *
 */
public class Not extends AbstractLogicalFunctionWith1Input {

	/**
	 * Constructs a NOT Function with 1 parameter with {@link FunctionDescription}
	 */
	public Not() {
		super("logical.not");
	}

	@Override
	protected Boolean compute(double value) {
		// if the given value is double missing -> return boolean missing
		if (Double.isNaN(value)) {
			return null;
		}
		// else if the value is zero (false) -> return true
		else if (Math.abs(value) < Double.MIN_VALUE * 2) {
			return true;
		} else {
			// if the given value is not zero (true) -> return false
			return false;
		}
	}

	@Override
	protected Boolean compute(Boolean value) {
		// if the given value is missing -> return missing
		if (value == null) {
			return null;
			// if the given value is true -> return false
		} else if (value) {
			return false;
			// if the given value is false -> return true
		} else {
			return true;
		}
	}

}
