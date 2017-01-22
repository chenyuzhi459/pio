package io.sugo.pio.tools.expression.internal.function.trigonometric;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 *
 * A {@link Function} computing the trigonometric arc tangent of an angle.
 *
 * @author Denis Schernov
 *
 */
public class ArcTangent extends Abstract1DoubleInputFunction {

	public ArcTangent() {
		super("trigonometrical.atan", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		return Double.isNaN(value) ? Double.NaN : Math.atan(value);
	}

}
