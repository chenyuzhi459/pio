package io.sugo.pio.tools.math.optimization.ec.es;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Selects a given fixed number of individuals by uniformly sampling from the current population
 * until the desired population size is reached.
 * 
 * @author Ingo Mierswa
 */
public class UniformSelection implements PopulationOperator {

	private int popSize;

	private boolean keepBest;

	private Random random;

	public UniformSelection(int popSize, boolean keepBest, Random random) {
		this.popSize = popSize;
		this.keepBest = keepBest;
		this.random = random;
	}

	/** The default implementation returns true for every generation. */
	public boolean performOperation(int generation) {
		return true;
	}

	@Override
	public void operate(Population population) {
		List<Individual> newGeneration = new LinkedList<Individual>();

		if (keepBest) {
			newGeneration.add(population.getBestEver());
		}

		while (newGeneration.size() < popSize) {
			newGeneration.add(population.get(random.nextInt(population.getNumberOfIndividuals())));
		}

		population.clear();
		population.addAll(newGeneration);
	}
}
