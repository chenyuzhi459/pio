package io.sugo.pio.tools.expression.internal.function.mathematical;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract1DoubleInputFunction;

/**
 * A {@link Function} computing the binary logarithm (base 2) of a number.
 *
 * @author Marcel Seifert
 *
 */
public class BinaryLogarithm extends Abstract1DoubleInputFunction {

	private static final double LOG2 = Math.log(2);

	public BinaryLogarithm() {
		super("mathematical.ld", Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value) {
		if (Double.isNaN(value) || value < 0) {
			return Double.NaN;
		} else {
			return Math.log(value) / LOG2;
		}
	}
}
