package io.sugo.pio.tools.math.optimization.ec.es;

/**
 * A population operator which can be applied on the population.
 * 
 * @author Ingo Mierswa Exp $
 */
public interface PopulationOperator {

	public void operate(Population pop);
}
