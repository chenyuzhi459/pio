package io.sugo.pio.tools.expression.internal.function.mathematical;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 *
 * A {@link Function} computing the square root of a number.
 *
 * @author David Arnu
 *
 */
public class SquareRoot extends Abstract1DoubleInputFunction {

	public SquareRoot() {
		super("mathematical.sqrt", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		return Math.sqrt(value);
	}

}
