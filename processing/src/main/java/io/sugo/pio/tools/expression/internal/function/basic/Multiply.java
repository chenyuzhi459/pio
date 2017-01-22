package io.sugo.pio.tools.expression.internal.function.basic;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract2DoubleInputFunction;

/**
 * A {@link Function} for multiplication.
 *
 * @author Gisa Schaefer
 *
 */
public class Multiply extends Abstract2DoubleInputFunction {

	/**
	 * Constructs a multiplication function.
	 */
	public Multiply() {
		super("basic.multiplication", 2, Ontology.NUMERICAL);
	}

	@Override
	protected double compute(double value1, double value2) {
		return value1 * value2;
	}

}
