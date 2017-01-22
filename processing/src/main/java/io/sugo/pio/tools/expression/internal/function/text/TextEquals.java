package io.sugo.pio.tools.expression.internal.function.text;

/**
 * A {@link Function} to check whether two nominal values are equal.
 *
 *
 */
public class TextEquals extends Abstract2StringInputBooleanOutputFunction {

	/**
	 * Creates a function to check whether two nominal values are equal.
	 */
	public TextEquals() {
		super("text_information.equals");
	}

	@Override
	protected Boolean compute(String value1, String value2) {
		if (value1 == null || value2 == null) {
			return null;
		}
		return value1.equals(value2);
	}

}
