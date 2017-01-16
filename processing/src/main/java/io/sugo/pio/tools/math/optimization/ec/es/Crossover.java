package io.sugo.pio.tools.math.optimization.ec.es;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Crossover operator for the values of an evolution strategies optimization. An individual is
 * selected with a given fixed propability and a mating partner is determined randomly. This class
 * only impplements uniform crossover.
 * 
 * @author Ingo Mierswa
 */
public class Crossover implements PopulationOperator {

	private double prob;

	private Random random;

	public Crossover(double prob, Random random) {
		this.prob = prob;
		this.random = random;
	}

	public void crossover(Individual i1, Individual i2) {
		double[] values1 = i1.getValues();
		double[] values2 = i2.getValues();
		boolean[] swap = new boolean[values1.length];
		for (int i = 0; i < swap.length; i++) {
			swap[i] = random.nextBoolean();
		}
		for (int i = 0; i < swap.length; i++) {
			if (swap[i]) {
				double dummy = values1[i];
				values1[i] = values2[i];
				values2[i] = dummy;
			}
		}
		i1.setValues(values1);
		i2.setValues(values2);
	}

	@Override
	public void operate(Population population) {
		if (population.getNumberOfIndividuals() < 2) {
			return;
		}

		List<Individual> matingPool = new LinkedList<Individual>();
		for (int i = 0; i < population.getNumberOfIndividuals(); i++) {
			matingPool.add((Individual) population.get(i).clone());
		}

		List<Individual> l = new LinkedList<Individual>();
		while (matingPool.size() > 1) {
			Individual p1 = matingPool.remove(random.nextInt(matingPool.size()));
			Individual p2 = matingPool.remove(random.nextInt(matingPool.size()));
			if (random.nextDouble() < prob) {
				crossover(p1, p2);
				l.add(p1);
				l.add(p2);
			}
		}
		population.addAll(l);
	}
}
