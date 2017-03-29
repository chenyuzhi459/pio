package io.sugo.pio.tools.math.optimization.ec.es;

/**
 * A mutation operator which can be applied on the population.
 * 
 * @author Ingo Mierswa
 */
public interface Mutation extends PopulationOperator {

	public void setValueType(int index, OptimizationValueType type);

}
