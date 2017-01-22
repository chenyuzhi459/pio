package io.sugo.pio.tools.expression.internal.function.text;


import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.AbstractArbitraryStringInputStringOutputFunction;

/**
 * A {@link Function} which replaces all parts of a nominal value which match a specific string.
 *
 * @author Thilo Kamradt
 *
 */
public class Replace extends AbstractArbitraryStringInputStringOutputFunction {

	/**
	 * Creates a function that replaces all parts of a nominal value which match a specific string
	 */
	public Replace() {
		super("text_transformation.replace", 3);
	}

	private String compute(String text, String target, String replacement) {

		// missing values are not changed
		if (text == null || target == null || replacement == null) {
			return null;
		}
		if (target.length() == 0) {
			throw new FunctionInputException("expression_parser.function_missing_arguments", "search", getFunctionName());
		}
		return text.replace(target, replacement);
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
