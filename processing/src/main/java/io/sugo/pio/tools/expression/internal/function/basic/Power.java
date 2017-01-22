package io.sugo.pio.tools.expression.internal.function.basic;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.internal.function.Abstract2DoubleInputFunction;

/**
 * A {@link Function} for computer powers of numbers.
 *
 * @author Gisa Schaefer
 *
 */
public class Power extends Abstract2DoubleInputFunction {

	/**
	 * Constructs a power function.
	 */
	public Power() {
		super("basic.power", 2, Ontology.NUMERICAL);
	}

	protected Power(String i18n, int numberOfArgumentsToCheck, int returnType) {
		super(i18n, numberOfArgumentsToCheck, returnType);
	}

	@Override
	protected double compute(double value1, double value2) {
		return Math.pow(value1, value2);
	}

}
