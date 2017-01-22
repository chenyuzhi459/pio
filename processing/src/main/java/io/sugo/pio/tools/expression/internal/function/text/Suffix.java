package io.sugo.pio.tools.expression.internal.function.text;



/**
 * A {@link Function} which computes the suffix of the given length from a nominal value.
 *
 * @author Thilo Kamradt
 *
 */
public class Suffix extends AbstractStringIntegerInputStringOutputFunction {

	/**
	 * Creates a function which computes the suffix of a given length from a nominal value
	 */
	public Suffix() {
		super("text_transformation.suffix", 2);
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
		} else if (index >= text.length() || index < 0) {
			return text;
		}
		return text.substring(Math.max(0, text.length() - (int) index));
	}

}
