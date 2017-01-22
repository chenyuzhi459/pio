package io.sugo.pio.tools.expression.internal.function.text;


import io.sugo.pio.tools.expression.FunctionInputException;

import java.util.regex.PatternSyntaxException;

/**
 * A {@link Function} for checking whether a nominal value matches a regular expression
 *
 * @author Thilo Kamradt
 *
 */
public class Matches extends Abstract2StringInputBooleanOutputFunction {

	/**
	 * Creates a function for checking whether a nominal value matches a regular expression
	 */
	public Matches() {
		super("text_information.matches");
	}

	@Override
	protected Boolean compute(String value1, String value2) {
		if (value1 == null || value2 == null) {
			return null;
		}
		try {
			return value1.matches(value2);
		} catch (PatternSyntaxException e) {
			throw new FunctionInputException("expression_parser.invalid_regex", value2, getFunctionName());
		}
	}

}
