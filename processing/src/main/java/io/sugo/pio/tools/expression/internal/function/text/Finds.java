package io.sugo.pio.tools.expression.internal.function.text;


import io.sugo.pio.tools.expression.FunctionInputException;

import java.util.regex.PatternSyntaxException;

/**
 * This {@link Function} checks whether any substring of a nominal Value matches a regular
 * expression.
 *
 * @author Thilo Kamradt
 *
 */
public class Finds extends Abstract2StringInputBooleanOutputFunction {

	/**
	 * Creates a function which checks whether any substring of a nominal Value matches a regular
	 * expression
	 */
	public Finds() {
		super("text_information.finds");
	}

	@Override
	protected Boolean compute(String value1, String value2) {
		if (value1 == null || value2 == null) {
			return null;
		}
		try {
			return value1.matches(".*" + value2 + ".*");
		} catch (PatternSyntaxException e) {
			throw new FunctionInputException("process.error.invalid_regex", value2);
		}
	}
}
