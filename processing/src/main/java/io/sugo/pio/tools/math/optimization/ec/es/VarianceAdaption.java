package io.sugo.pio.tools.math.optimization.ec.es;

import java.util.Iterator;
import java.util.LinkedList;


/**
 * Implements the 1/5-Rule for dynamic parameter adaption of the variance of a
 * {@link GaussianMutation}. The interval size should have the same size as the changable
 * components, i.e. the number of examples (alphas).
 * 
 * @author Ingo Mierswa
 */
public class VarianceAdaption implements PopulationOperator {

	/**
	 * Waits this number of intervals before variance adaption is applied. Usually 10.
	 */
	private static final int WAIT_INTERVALS = 2;

	/** Used factor for shrinking and enlarging. Usually 0.85. */
	private static final double FACTOR = 0.85;

	/** The mutation. */
	private GaussianMutation mutation = null;

	/** The interval size in which the new variance is calculated. */
	private int intervalSize;

	/** Remember for all positions if an improval was found. */
	private LinkedList<Boolean> successList = new LinkedList<Boolean>();

	/**
	 * The interval size should be as big as the changeable components, i.e. the number of examples
	 * (alphas).
	 */
	public VarianceAdaption(GaussianMutation mutation, int intervalSize) {
		this.mutation = mutation;
		this.intervalSize = intervalSize;
	}

	@Override
	public void operate(Population population) {
		if (population.getGenerationsWithoutImprovement() < 2) {
			successList.add(true);
		} else {
			successList.add(false);
		}

		if (population.getGeneration() >= WAIT_INTERVALS * intervalSize) {
			successList.removeFirst();
			if ((population.getGeneration() % intervalSize) == 0) {
				int successCount = 0;
				Iterator<Boolean> i = successList.iterator();
				while (i.hasNext()) {
					if (i.next()) {
						successCount++;
					}
				}

				if (((double) successCount / (double) (WAIT_INTERVALS * intervalSize)) < 0.2) {
					double[] sigma = mutation.getSigma();
					for (int s = 0; s < sigma.length; s++) {
						sigma[s] *= FACTOR;
					}
					mutation.setSigma(sigma);
				} else {
					double[] sigma = mutation.getSigma();
					for (int s = 0; s < sigma.length; s++) {
						sigma[s] /= FACTOR;
					}
					mutation.setSigma(sigma);
				}
			}
		}
	}
}
