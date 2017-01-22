package io.sugo.pio.tools.expression.internal.function.text;

/**
 * A {@link Function} to check whether a nominal value ends with a specific string.
 *
 *
 */
public class Ends extends Abstract2StringInputBooleanOutputFunction {

	/**
	 * Creates a function to check whether a nominal value ends with a specific string.
	 */
	public Ends() {
		super("text_information.ends");
	}

	@Override
	protected Boolean compute(String value1, String value2) {
		if (value1 == null || value2 == null) {
			return null;
		}
		return value1.endsWith(value2);
	}

}
