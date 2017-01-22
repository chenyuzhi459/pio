package io.sugo.pio.tools.expression.internal.function.trigonometric;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 *
 * A {@link Function} computing the trigonometric tangent of an angle.
 *
 * @author Denis Schernov
 *
 */
public class Tangent extends Abstract1DoubleInputFunction {

	public Tangent() {
		super("trigonometrical.tan", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		return Double.isNaN(value) ? Double.NaN : Math.tan(value);
	}
}
