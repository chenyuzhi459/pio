package io.sugo.pio.tools.math.optimization.ec.es;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Performs tournaments with k participants. The winner of each tournament is added to the next
 * population. This will be repeated until the desired population size is reached. The tournament
 * size k represents the selection pressure. For small sizes (like 2) relatively bad individuals
 * have a good chance to survive. If k reaches the population size, only the best individual will
 * survive.
 * 
 * @author Ingo Mierswa Exp $
 */
public class TournamentSelection implements PopulationOperator {

	private double popSize;

	private double tournamentFraction;

	private boolean keepBest = false;

	private Random random;

	public TournamentSelection(int popSize, double tournamentFraction, boolean keepBest, Random random) {
		this.popSize = popSize;
		this.keepBest = keepBest;
		this.tournamentFraction = tournamentFraction;
		this.random = random;
	}

	@Override
	public void operate(Population population) {
		List<Individual> newGeneration = new LinkedList<Individual>();
		if (population.getNumberOfIndividuals() > 0) {
			int tournamentSize = Math.max((int) Math.round(population.getNumberOfIndividuals() * tournamentFraction), 1);
			if (keepBest && (population.getBestEver() != null)) {
				newGeneration.add(population.getBestEver());
			}

			while (newGeneration.size() < this.popSize) {
				Individual winner = null;
				for (int k = 0; k < tournamentSize; k++) {
					Individual current = population.get(random.nextInt(population.getNumberOfIndividuals()));
					if ((winner == null) || (current.getFitnessValues()[0] > winner.getFitnessValues()[0])) {
						winner = current;
					}
				}
				newGeneration.add(winner);
			}
		}
		population.clear();
		population.addAll(newGeneration);
	}
}
