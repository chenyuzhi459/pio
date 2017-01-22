package io.sugo.pio.tools.expression.internal.function.trigonometric;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 *
 * A {@link Function} computing the trigonometric secant of a radian.
 *
 * @author Denis Schernov
 *
 */
public class Secant extends Abstract1DoubleInputFunction {

	public Secant() {
		super("trigonometrical.sec", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		return Double.isNaN(value) || value == Math.PI / 2 ? Double.NaN : 1.0 / Math.cos(value);
	}
}
