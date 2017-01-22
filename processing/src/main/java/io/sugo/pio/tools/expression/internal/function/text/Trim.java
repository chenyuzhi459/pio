package io.sugo.pio.tools.expression.internal.function.text;


import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.AbstractArbitraryStringInputStringOutputFunction;

/**
 * A {@link Function} which removes all spaces, tabs and new lines from the end and the beginning of
 * a nominal value.
 *
 * @author Thilo Kamradt
 *
 */
public class Trim extends AbstractArbitraryStringInputStringOutputFunction {

	/**
	 * Creates a function which removes all spaces, tabs and new lines from the end and the
	 * beginning of a nominal value
	 */
	public Trim() {
		super("text_transformation.trim", 1);
	}

	@Override
	protected String compute(String... values) {
		if (values[0] == null) {
			return null;
		}
		return values[0].trim();
	}

	@Override
	protected void checkNumberOfInputs(int length) {
		if (length != 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 1, length);
		}
	}

}
