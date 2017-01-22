package io.sugo.pio.tools.expression.internal.function.statistical;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.FunctionDescription;
import io.sugo.pio.tools.expression.internal.function.AbstractArbitraryDoubleInputFunction;

/**
 *
 * A {@link Function} for maximum.
 *
 * @author David Arnu
 *
 */
public class Maximum extends AbstractArbitraryDoubleInputFunction {

	/**
	 * Constructs an minimum function.
	 */
	public Maximum() {
		super("statistical.max", FunctionDescription.UNFIXED_NUMBER_OF_ARGUMENTS, Ontology.NUMERICAL);

	}

	@Override
	public double compute(double... values) {

		double max = values[0];

		for (int i = 1; i < values.length; i++) {
			max = max > values[i] ? max : values[i];
		}
		return max;
	}

}
