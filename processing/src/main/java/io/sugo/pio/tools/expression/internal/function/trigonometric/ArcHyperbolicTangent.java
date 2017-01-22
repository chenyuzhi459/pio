package io.sugo.pio.tools.expression.internal.function.trigonometric;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;
import org.apache.commons.math3.util.FastMath;

/**
 *
 * A {@link Function} computing the trigonometric arc hyperbolic tangent of an angle.
 *
 * @author Denis Schernov
 *
 */
public class ArcHyperbolicTangent extends Abstract1DoubleInputFunction {

	public ArcHyperbolicTangent() {
		super("trigonometrical.atanh", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		return Double.isNaN(value) ? Double.NaN : FastMath.atanh(value);
	}
}
