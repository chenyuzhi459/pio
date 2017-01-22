package io.sugo.pio.tools.expression.internal.function.trigonometric;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 *
 * A {@link Function} computing the trigonometric hyperbolic cosine of an angle.
 *
 * @author Denis Schernov
 *
 */
public class HyperbolicCosine extends Abstract1DoubleInputFunction {

	public HyperbolicCosine() {
		super("trigonometrical.cosh", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		return Double.isNaN(value) ? Double.NaN : Math.cosh(value);
	}

}
