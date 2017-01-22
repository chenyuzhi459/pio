package io.sugo.pio.tools.expression.internal.function.text;


import io.sugo.pio.tools.Tools;
import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.AbstractArbitraryStringInputStringOutputFunction;

/**
 * A {@link Function} which escapse all HTML tags of a nominal value.
 *
 * @author Thilo Kamradt
 *
 */
public class EscapeHTML extends AbstractArbitraryStringInputStringOutputFunction {

	/**
	 * Creates a function which escapse all HTML tags of a nominal value.
	 */
	public EscapeHTML() {
		super("text_transformation.escape_html", 1);
	}

	@Override
	protected String compute(String... values) {
		return Tools.escapeHTML(values[0]);
	}

	@Override
	protected void checkNumberOfInputs(int length) {
		if (length != 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 1, length);
		}
	}

}
