package io.sugo.pio.tools.expression.internal.function.text;



/**
 * A {@link Function} which delivers the character at a specific position of a nominal value.
 *
 * @author Thilo Kamradt
 *
 */
public class CharAt extends AbstractStringIntegerInputStringOutputFunction {

	/**
	 * Creates a function which delivers the character at a specific position of a nominal value
	 */
	public CharAt() {
		super("text_transformation.char", 2);
	}

	/**
	 * Delivers the character at the specified index.
	 *
	 * @param text
	 *            the text from which you want to separate the character
	 * @param index
	 *            the index of the character you want to separate
	 * @return the character at the specified index.
	 */
	@Override
	protected String compute(String text, double index) {
		if (text == null || index >= text.length() || index < 0) {
			return null;
		}
		return "" + text.charAt((int) index);
	}

}
