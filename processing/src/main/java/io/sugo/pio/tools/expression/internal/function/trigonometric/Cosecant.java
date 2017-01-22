package io.sugo.pio.tools.expression.internal.function.trigonometric;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 *
 * A {@link Function} computing the trigonometric cosecant of a radian.
 *
 * @author Denis Schernov
 *
 */
public class Cosecant extends Abstract1DoubleInputFunction {

	public Cosecant() {
		super("trigonometrical.cosec", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		return Double.isNaN(value) | value == 0 | value % Math.PI == 0 ? Double.NaN : 1.0 / Math.sin(value);
	}
}
