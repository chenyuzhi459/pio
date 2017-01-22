package io.sugo.pio.tools.expression.internal.function.text;


import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.AbstractArbitraryStringInputStringOutputFunction;

/**
 * A {@link Function} for replacing parts of a string by an other. Which parts replaced parts is
 * defined by a regular expression
 *
 * @author David Arnu
 *
 */
public class ReplaceAll extends AbstractArbitraryStringInputStringOutputFunction {

	/**
	 * Creates a function for replacing parts of a string by an other
	 */
	public ReplaceAll() {
		super("text_transformation.replace_all", 3);
	}

	/**
	 * Replaces all occurences of <code>regex</code> in <code>text</code> with
	 * <code>replacement</code>
	 *
	 * @param text
	 * @param regex
	 * @param replacement
	 * @return the string with replacements
	 */
	protected String compute(String text, String regex, String replacement) {

		// missing values are not changed
		if (text == null || regex == null || replacement == null) {
			return null;
		} else {
			if (regex.length() == 0) {
				throw new FunctionInputException("expression_parser.function_missing_arguments", "regex", getFunctionName());
			}
			return text.replaceAll(regex, replacement);
		}
	}

	@Override
	protected String compute(String... values) {
		return compute(values[0], values[1], values[2]);
	}

	/**
	 * Checks if the number of input arguments is exactly 3.
	 */
	@Override
	protected void checkNumberOfInputs(int inputLength) {
		if (inputLength != 3) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 3, inputLength);
		}
	}

}
