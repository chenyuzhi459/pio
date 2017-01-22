package io.sugo.pio.tools.expression.internal.function.statistical;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.FunctionDescription;
import io.sugo.pio.tools.expression.internal.function.AbstractArbitraryDoubleInputFunction;

/**
 *
 * A {@link Function} for minimum.
 *
 * @author David Arnu
 *
 */
public class Minimum extends AbstractArbitraryDoubleInputFunction {

	/**
	 * Constructs an minimum function.
	 */
	public Minimum() {
		super("statistical.min", FunctionDescription.UNFIXED_NUMBER_OF_ARGUMENTS, Ontology.NUMERICAL);

	}

	@Override
	public double compute(double... values) {

		double min = values[0];

		for (int i = 1; i < values.length; i++) {
			min = min < values[i] ? min : values[i];
		}
		return min;
	}
}
