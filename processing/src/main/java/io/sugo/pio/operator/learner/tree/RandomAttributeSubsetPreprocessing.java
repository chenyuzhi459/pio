package io.sugo.pio.operator.learner.tree;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;


/**
 * Selects a random attribute subset.
 *
 */
public class RandomAttributeSubsetPreprocessing implements AttributePreprocessing {

	private Random random;

	private double subsetRatio;
	private boolean useHeuristicRation;

	/**
	 * Stores the parameters for using them in {@link #preprocess}.
	 *
	 * @param useHeuristicRation
	 *            if <code>true</code> the subsetRatio is ignored and <code>log(m) + 1</code> out of
	 *            <code>m</code> entries are randomly selected by {@link #preprocess}
	 * @param subsetRatio
	 *            the percentage of entries that are randomly selected by {@link #preprocess} - only
	 *            used if useHeuristicRatio is <code>false</code>
	 * @param random
	 *            the {@link Random} used for the random selection of the entries in
	 *            {@link #preprocess}
	 */
	public RandomAttributeSubsetPreprocessing(boolean useHeuristicRation, double subsetRatio, Random random) {
		this.subsetRatio = subsetRatio;
		this.random = random;
		this.useHeuristicRation = useHeuristicRation;
	}

	@Override
	public int[] preprocess(int[] attributeSelection) {

		double usedSubsetRatio = subsetRatio;
		if (useHeuristicRation) {
			double desiredNumber = Math.floor(Math.log(attributeSelection.length) / Math.log(2) + 1);
			usedSubsetRatio = desiredNumber / attributeSelection.length;
		}

		List<Integer> indexSubset = new LinkedList<>();

		for (int i = 0; i < attributeSelection.length; i++) {
			if (random.nextDouble() <= usedSubsetRatio) {
				indexSubset.add(i);
			}
		}

		// ensure that at least one attribute is left
		if (indexSubset.size() == 0) {
			int index = random.nextInt(attributeSelection.length);
			return new int[] { attributeSelection[index] };
		}

		// store the entries at the selected indices
		int[] attributeSubset = new int[indexSubset.size()];
		int counter = 0;
		for (int index : indexSubset) {
			attributeSubset[counter] = attributeSelection[index];
			counter++;
		}

		return attributeSubset;
	}

}
