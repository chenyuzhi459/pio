package io.sugo.pio.tools.expression.internal.function.mathematical;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 * A {@link Function} computing the common logarithm (base 10) of a number.
 *
 * @author Marcel Seifert
 *
 */
public class CommonLogarithm extends Abstract1DoubleInputFunction {

	public CommonLogarithm() {
		super("mathematical.log", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		return Math.log10(value);
	}

}
