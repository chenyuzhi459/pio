package io.sugo.pio.tools.expression.internal.function.trigonometric;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 *
 * A {@link Function} computing the trigonometric cosine of an angle.
 *
 * @author Denis Schernov
 *
 */
public class Cosine extends Abstract1DoubleInputFunction {

	public Cosine() {
		super("trigonometrical.cos", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		return Double.isNaN(value) ? Double.NaN : Math.cos(value);
	}
}
