package io.sugo.pio.tools.expression.internal.function.text;



/**
 * A {@link Function} which checks if any part of a nominal value matches a specific string and
 * returns the index of the first matching letter.
 *
 * @author Thilo Kamradt
 *
 */
public class Index extends Abstract2StringInputIntegerOutputFunction {

	/**
	 * Creates a function which checks if any part of a nominal value matches a specific string and
	 * returns the index of the first matching letter.
	 */
	public Index() {
		super("text_information.index", 2);
	}

	@Override
	protected double compute(String value1, String value2) {
		if (value1 == null || value2 == null) {
			return Double.NaN;
		}
		return value1.indexOf(value2);
	}

}
