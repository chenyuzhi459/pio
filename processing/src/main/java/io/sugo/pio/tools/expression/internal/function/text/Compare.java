package io.sugo.pio.tools.expression.internal.function.text;



/**
 * A {@link Function} for comparing two Strings.
 *
 *
 */
public class Compare extends Abstract2StringInputIntegerOutputFunction {

	/**
	 * Creates a function for comparing two Strings
	 */
	public Compare() {
		super("text_information.compare", 2);
	}

	@Override
	protected double compute(String value1, String value2) {

		// if one of the two strings is a missing value, we also return missing
		if (value1 == null || value2 == null) {
			return Double.NaN;
		}
		return value1.compareTo(value2);
	}

}
