package io.sugo.pio.tools.expression.internal.function.basic;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract2DoubleInputFunction;

/**
 * A {@link Function} for computing the modulus.
 *
 * @author Gisa Schaefer
 *
 */
public class Modulus extends Abstract2DoubleInputFunction {

	/**
	 * Constructs a modulo function.
	 */
	public Modulus() {
		super("basic.modulus", 2, Ontology.NUMERICAL);
	}

	protected Modulus(String i18n, int numberOfArgumentsToCheck, int returnType) {
		super(i18n, numberOfArgumentsToCheck, returnType);
	}

	@Override
	protected double compute(double value1, double value2) {
		return value1 % value2;
	}

}
