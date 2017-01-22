package io.sugo.pio.tools.expression.internal.function.trigonometric;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 *
 * A {@link Function} computing the trigonometric arc cosine of an angle.
 *
 * @author Denis Schernov
 *
 */
public class ArcCosine extends Abstract1DoubleInputFunction {

	public ArcCosine() {
		super("trigonometrical.acos", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {

		return Double.isNaN(value) ? Double.NaN : Math.acos(value);
	}

}
