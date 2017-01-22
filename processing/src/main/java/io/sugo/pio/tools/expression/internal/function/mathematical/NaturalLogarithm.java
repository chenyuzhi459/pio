package io.sugo.pio.tools.expression.internal.function.mathematical;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 * A {@link Function} computing the natural logarithm (base e) of a number.
 *
 * @author Marcel Seifert
 *
 */
public class NaturalLogarithm extends Abstract1DoubleInputFunction {

	public NaturalLogarithm() {
		super("mathematical.ln", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		return Math.log(value);
	}

}
