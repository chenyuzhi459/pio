package io.sugo.pio.tools.expression.internal.function.statistical;


import io.sugo.pio.tools.Ontology;
import io.sugo.pio.tools.expression.FunctionDescription;
import io.sugo.pio.tools.expression.internal.function.AbstractArbitraryDoubleInputFunction;
import org.apache.commons.math3.stat.StatUtils;

/**
 *
 * A {@link Function} for sum.
 *
 * @author David Arnu
 *
 */
public class Sum extends AbstractArbitraryDoubleInputFunction {

	/**
	 * Constructs an sum function.
	 */
	public Sum() {
		super("statistical.sum", FunctionDescription.UNFIXED_NUMBER_OF_ARGUMENTS, Ontology.NUMERICAL);

	}

	@Override
	public double compute(double... values) {
		return StatUtils.sum(values);
	}
}
