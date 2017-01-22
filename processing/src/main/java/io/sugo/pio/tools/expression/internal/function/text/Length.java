package io.sugo.pio.tools.expression.internal.function.text;

/**
 * A {@link Function} which calculates the length of a nominal value.
 *
 * @author Thilo Kamradt
 *
 */
public class Length extends Abstract1StringInputIntegerOutputFunction {

	/**
	 * Creates a function which calculates the length of a nominal value.
	 */
	public Length() {
		super("text_information.length");
	}

	@Override
	protected double compute(String value1) {
		if (value1 == null) {
			return Double.NaN;
		}
		return value1.length();
	}

}
