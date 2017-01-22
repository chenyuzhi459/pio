package io.sugo.pio.tools.expression.internal.function.text;



/**
 * A {@link Function} which computes the prefix of a given length from a nominal value
 *
 * @author Thilo Kamradt
 *
 */
public class Prefix extends AbstractStringIntegerInputStringOutputFunction {

	/**
	 * Creates a function which computes the prefix of a given length from a nominal value
	 */
	public Prefix() {
		super("text_transformation.prefix", 2);
	}

	/**
	 * Computes the result.
	 *
	 * @param text
	 * @param index
	 * @return the result of the computation.
	 */
	@Override
	protected String compute(String text, double index) {
		if (text == null) {
			return null;
		} else if (Double.isNaN(index)) {
			// for compatibility reasons
			index = 0;
		} else if (index < 0 || index >= text.length()) {
			return text;
		}
		return text.substring(0, Math.min(text.length(), (int) index));

	}

}
