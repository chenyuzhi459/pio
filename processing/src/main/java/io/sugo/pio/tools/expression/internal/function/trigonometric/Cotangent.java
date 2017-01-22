package io.sugo.pio.tools.expression.internal.function.trigonometric;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 *
 * A {@link Function} computing the trigonometric cotangent of a radian.
 *
 * @author Denis Schernov
 *
 */
public class Cotangent extends Abstract1DoubleInputFunction {

	public Cotangent() {
		super("trigonometrical.cot", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		// for missing values or values where the cotangent is undefined return missing
		if (Double.isNaN(value) || Math.abs(value) < Double.MIN_VALUE || Math.abs(value % Math.PI) < Double.MIN_VALUE) {
			return Double.NaN;
		}
		return 1.0 / Math.tan(value);
	}
}
