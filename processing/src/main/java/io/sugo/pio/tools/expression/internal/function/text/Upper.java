package io.sugo.pio.tools.expression.internal.function.text;


import io.sugo.pio.tools.expression.FunctionInputException;
import io.sugo.pio.tools.expression.internal.function.AbstractArbitraryStringInputStringOutputFunction;

import java.util.Locale;

/**
 * A {@link Function} which transforms a nominal value to its upper case representation.
 *
 * @author Thilo Kamradt
 *
 */
public class Upper extends AbstractArbitraryStringInputStringOutputFunction {

	/**
	 * Creates a function that transforms a nominal value to its upper case representation
	 */
	public Upper() {
		super("text_transformation.upper", 1);
	}

	@Override
	protected String compute(String... values) {
		if (values[0] == null) {
			return null;
		}
		return values[0].toUpperCase(Locale.ENGLISH);
	}

	@Override
	protected void checkNumberOfInputs(int length) {
		if (length != 1) {
			throw new FunctionInputException("expression_parser.function_wrong_input", getFunctionName(), 1, length);
		}
	}

}
