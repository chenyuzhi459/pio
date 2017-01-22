package io.sugo.pio.tools.expression.internal.function.text;


import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.AbstractArbitraryStringInputStringOutputFunction;

import java.util.Locale;

/**
 * A {@link Function} which transforms a nominal value to its lower case representation.
 *
 * @author Thilo Kamradt
 *
 */
public class Lower extends AbstractArbitraryStringInputStringOutputFunction {

	/**
	 * Creates a function that transforms a nominal value to its lower case representation
	 */
	public Lower() {
		super("text_transformation.lower", 1);
	}

	@Override
	protected String compute(String... values) {
		if (values[0] == null) {
			return null;
		}
		return values[0].toLowerCase(Locale.ENGLISH);
	}

	@Override
	protected void checkNumberOfInputs(int length) {
		if (length != 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 1, length);
		}
	}

}
