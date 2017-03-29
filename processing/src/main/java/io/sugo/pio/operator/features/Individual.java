package io.sugo.pio.operator.features;


import io.sugo.pio.operator.performance.PerformanceVector;

/**
 * Individuals contain all necessary informations for feature selection or weighting of example sets
 * for population based search heuristics, including the performance. Each individiual can also
 * handle a crowding distance for multi-objecitve optimization approaches.
 * 
 * @author Ingo Mierswa
 */
public class Individual {

	/** The weight mask. */
	private double[] weights;

	/**
	 * The performance this example set has achieved during evaluation. Null if no evaluation has
	 * been performed so far.
	 */
	private PerformanceVector performanceVector = null;

	/** The crowding distance can used for multiobjective optimization schemes. */
	private double crowdingDistance = Double.NaN;

	/** Creates a new individual by cloning the given values. */
	public Individual(double[] weights) {
		this.weights = weights;
	}

	public double[] getWeights() {
		return this.weights;
	}

	public double[] getWeightsClone() {
		double[] clone = new double[weights.length];
		System.arraycopy(this.weights, 0, clone, 0, this.weights.length);
		return clone;
	}

	public int getNumberOfUsedAttributes() {
		int count = 0;
		for (double d : this.weights) {
			if (d > 0) {
				count++;
			}
		}
		return count;
	}

	public PerformanceVector getPerformance() {
		return performanceVector;
	}

	public void setPerformance(PerformanceVector performanceVector) {
		this.performanceVector = performanceVector;
	}

	public double getCrowdingDistance() {
		return this.crowdingDistance;
	}

	public void setCrowdingDistance(double crowdingDistance) {
		this.crowdingDistance = crowdingDistance;
	}
}
