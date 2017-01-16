package io.sugo.pio.tools.math.optimization.ec.es;

import java.util.Random;


/**
 * Like RouletteWheel this population operator selects a given fixed number of individuals by
 * subdividing a roulette wheel in sections of size proportional to the individuals' fitness values.
 * The fitness values are filtered according to the Boltzmann theorem.
 * 
 * @author Ingo Mierswa
 */
public class BoltzmannSelection extends RouletteWheel {

	private double temperature;

	private double delta = 0.0d;

	public BoltzmannSelection(int popSize, double temperature, int maxGenerations, boolean dynamic, boolean keepBest,
			Random random) {
		super(popSize, keepBest, random);
		this.temperature = temperature;
		if (dynamic) {
			this.delta = this.temperature / (maxGenerations + 1);
		}
	}

	/**
	 * Returns a fitness based on the Boltzmann theorem, i.e. exp(fitness/temperature). Like for
	 * simulated annealing the temperature should decrease over time.
	 */
	@Override
	public double filterFitness(double fitness) {
		return Math.exp(fitness / temperature);
	}

	/**
	 * Applies the method from the superclass and decreases the temperature in the adaptive case.
	 */
	@Override
	public void operate(Population population) {
		super.operate(population);
		this.temperature -= delta;
	}
}
