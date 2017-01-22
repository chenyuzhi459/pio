package io.sugo.pio.tools.expression.internal.function.text;

/**
 * A {@link Function} to check whether a nominal value starts with a specific string.
 *
 *
 */
public class Starts extends Abstract2StringInputBooleanOutputFunction {

	/**
	 * Creates a function to check whether a nominal value starts with a specific string
	 */
	public Starts() {
		super("text_information.starts");
	}

	@Override
	protected Boolean compute(String value1, String value2) {
		if (value1 == null || value2 == null) {
			return null;
		}
		return value1.startsWith(value2);
	}

}
