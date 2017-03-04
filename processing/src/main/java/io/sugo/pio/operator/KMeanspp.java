package io.sugo.pio.operator;

import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.clustering.ClusterModel;
import io.sugo.pio.operator.clustering.clusterer.RMAbstractClusterer;
import io.sugo.pio.tools.RandomGenerator;
import io.sugo.pio.tools.math.similarity.DistanceMeasure;


/**
 * This algorithm is the first part of K-Means++ descried in the paper
 * "k-means++: The Advantages of Careful Seeding" by David Arther and Sergei Vassilvitskii
 * 
 *
 */
public class KMeanspp extends RMAbstractClusterer {

	/** Short description for GUI */
	public static final String SHORT_DESCRIPTION = "Determine the first k centroids using the K-Means++ heuristic described in \"k-means++: The Advantages of Careful Seeding\" by David Arthur and Sergei Vassilvitskii 2007";

	/** Label for button */
	public static final String PARAMETER_USE_KPP = "determine_good_start_values";

	/** ExampleSet to work on */
	private ExampleSet exampleSet = null;

	/** DistanceMeasure to use */
	private DistanceMeasure measure = null;

	private RandomGenerator generator = null;
	private int examplesize = -1;
	private int minK = 0;

	/**
	 * Initialization of K-Means++
	 * 
	 * @param anz
	 *            initial Cluster count
	 * @param es
	 *            ExampleSet to work on
	 * @param measure
	 *            DistanceMeasure to use
	 * @throws OperatorException
	 */
	public KMeanspp(int anz, ExampleSet es, DistanceMeasure measure,
                    RandomGenerator generator) throws OperatorException {
		super();

		this.minK = anz;
		this.exampleSet = es;
		this.examplesize = es.size();
		this.measure = measure;
		this.generator = generator;
	}

	/**
	 * start the algorithm
	 * 
	 * @return array with Ids of the centroids
	 * @throws ProcessStoppedException
	 */
	public int[] getStart() throws ProcessStoppedException {
		int[] ret = new int[minK];
		int i = 0;
		int anz = 0;

		// take the first Centroid at random
		for (Integer index : generator.nextIntSetWithRange(0, exampleSet.size(), 1)) {
			ret[anz] = index;
			anz++;
			i = index;
		}

		while (anz < minK) {
			boolean again = false;
			checkForStop();

			do {
				checkForStop();
				again = false;
				double[] shortest = new double[examplesize];
				double maxProb = 0;
				int maxPorbId = -1;
				double distSum = 0;
				// sum of shortest path between chosen centroids an all Points
				for (int j = 0; j < examplesize; j++) {
					double minDist = -1;
					Example ex = exampleSet.getExample(j);
					for (Integer id : ret) {
						double dist = measure.calculateDistance(ex, exampleSet.getExample(id));
						if (minDist == -1 || minDist > dist) {
							minDist = dist;
						}
					}
					distSum += minDist;
					shortest[j] = minDist;
				}

				// get maximal Probability
				for (int j = 0; j < examplesize; j++) {
					double prob = Math.pow(shortest[j], 2) / Math.pow(distSum, 2);
					if (prob > maxProb) {
						maxPorbId = j;
						maxProb = prob;
					}
				}

				i = maxPorbId;
				for (Integer id : ret) {
					if (id == i) {
						again = true;
					}
				}
			} while (again);
			ret[anz] = i;
			anz++;
		}

		return ret;
	}

	@Override
	public ClusterModel generateClusterModel(ExampleSet exampleSet) throws OperatorException {
		return null;
	}

	@Override
	public String getDefaultFullName() {
		return null;
	}

	@Override
	public OperatorGroup getGroup() {
		return null;
	}

	@Override
	public String getDescription() {
		return null;
	}
}
