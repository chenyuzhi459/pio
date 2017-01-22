package io.sugo.pio.tools.expression.internal.function.trigonometric;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 *
 * A {@link Function} computing the trigonometric arc sine of an angle.
 *
 * @author Denis Schernov
 *
 */
public class ArcSine extends Abstract1DoubleInputFunction {

	public ArcSine() {
		super("trigonometrical.asin", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		return Double.isNaN(value) ? Double.NaN : Math.asin(value);
	}

}
