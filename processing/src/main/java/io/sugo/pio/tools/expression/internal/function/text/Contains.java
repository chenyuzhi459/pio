package io.sugo.pio.tools.expression.internal.function.text;

/**
 * A {@link Function} for checking if one String contains the other.
 *
 *
 */
public class Contains extends Abstract2StringInputBooleanOutputFunction {

	/**
	 * Creates a function for checking if one String contains the other
	 */
	public Contains() {
		super("text_information.contains");

	}

	@Override
	protected Boolean compute(String value1, String value2) {
		if (value1 == null || value2 == null) {
			// if one of the two strings represent a missing value, the result is also missing
			return null;
		}
		return value1.contains(value2);
	}
}
