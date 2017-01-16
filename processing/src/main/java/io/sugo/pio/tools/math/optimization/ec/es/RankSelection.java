package io.sugo.pio.tools.math.optimization.ec.es;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Selects a given fixed number of individuals by subdividing a roulette wheel in sections of size
 * proportional to the individuals' rank based on their fitness values. Optionally keep the best
 * individual. Since the individuals are sorted accordingly to their rank this selection operator
 * needs m log m time for population size m.
 * 
 * @author Ingo Mierswa
 */
public class RankSelection implements PopulationOperator {

	private int popSize;

	private boolean keepBest;

	private Random random;

	public RankSelection(int popSize, boolean keepBest, Random random) {
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

		population.sort();

		double fitnessSum = (population.getNumberOfIndividuals() * (population.getNumberOfIndividuals() + 1)) / 2.0d; // sum
																														// of
																														// number
																														// of
																														// individuals

		while (newGeneration.size() < popSize) {
			double r = fitnessSum * random.nextDouble();
			int j = -1;
			double f = 0;
			do {
				j++;
				f += j;
			} while (f < r);
			newGeneration.add(population.get(j));
		}

		population.clear();
		population.addAll(newGeneration);
	}
}
