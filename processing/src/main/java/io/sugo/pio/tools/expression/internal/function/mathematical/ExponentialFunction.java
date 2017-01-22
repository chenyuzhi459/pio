package io.sugo.pio.tools.expression.internal.function.mathematical;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 * A {@link Function} computing Euler's number e raised to the power of a double value of a number.
 *
 * @author Marcel Seifert
 *
 */
public class ExponentialFunction extends Abstract1DoubleInputFunction {

	public ExponentialFunction() {
		super("mathematical.exp", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		return Math.exp(value);
	}

}
