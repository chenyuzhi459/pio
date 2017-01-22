package io.sugo.pio.tools.expression.internal.function.text;


import io.sugo.pio.tools.expression.FunctionDescription;
import io.sugo.pio.tools.expression.internal.function.AbstractArbitraryStringInputStringOutputFunction;

/**
 * A {@link Function} for concatenating Strings.
 *
 * @author David Arnu
 *
 */
public class Concat extends AbstractArbitraryStringInputStringOutputFunction {

	/**
	 * Creates a function for concatenating Strings.
	 */
	public Concat() {
		super("text_transformation.concat", FunctionDescription.UNFIXED_NUMBER_OF_ARGUMENTS);
	}

	@Override
	protected String compute(String... values) {

		StringBuilder builder = new StringBuilder();

		for (String value : values) {
			// missing values are ignored
			if (value != null) {
				builder.append(value);
			}
		}
		return builder.toString();
	}

	@Override
	protected void checkNumberOfInputs(int length) {
		// we do not care about the number of attributes
	}

}
