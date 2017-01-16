package io.sugo.pio.tools.math.optimization.ec.es;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Selects a given fixed number of individuals by subdividing a roulette wheel in sections of size
 * proportional to the individuals' fitness values. Optionally keep the best individual.
 * 
 * @author Ingo Mierswa
 */
public class RouletteWheel implements PopulationOperator {

	private int popSize;

	private boolean keepBest;

	private Random random;

	public RouletteWheel(int popSize, boolean keepBest, Random random) {
		this.popSize = popSize;
		this.keepBest = keepBest;
		this.random = random;
	}

	/** The default implementation returns true for every generation. */
	public boolean performOperation(int generation) {
		return true;
	}

	/**
	 * Subclasses may override this method and recalculate the fitness based on the given one, e.g.
	 * Boltzmann selection or scaled selection. The default implementation simply returns the given
	 * fitness.
	 */
	public double filterFitness(double fitness) {
		return fitness;
	}

	@Override
	public void operate(Population population) {
		List<Individual> newGeneration = new LinkedList<Individual>();

		if (keepBest) {
			newGeneration.add(population.getBestEver());
		}

		double fitnessSum = 0;
		for (int i = 0; i < population.getNumberOfIndividuals(); i++) {
			fitnessSum += filterFitness(population.get(i).getFitness().getMainCriterion().getFitness());
		}

		while (newGeneration.size() < popSize) {
			double r = fitnessSum * random.nextDouble();
			int j = 0;
			double f = 0;
			Individual individual = null;
			do {
				if (j >= population.getNumberOfIndividuals()) {
					break;
				}
				individual = population.get(j++);
				f += filterFitness(individual.getFitness().getMainCriterion().getFitness());
			} while (f < r);
			newGeneration.add(individual);
		}

		population.clear();
		population.addAll(newGeneration);
	}
}
