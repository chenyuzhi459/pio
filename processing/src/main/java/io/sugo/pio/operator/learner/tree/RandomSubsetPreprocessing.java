package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.tools.RandomGenerator;

import java.util.Iterator;

/**
 * Selects a random subset.
 * 
 * @author Ingo Mierswa
 */
public class RandomSubsetPreprocessing implements SplitPreprocessing {

	private RandomGenerator random;

	private double subsetRatio = 0.2;
	private boolean useHeuristicRation;

	public RandomSubsetPreprocessing(boolean useHeuristicRation, double subsetRatio, RandomGenerator random) {
		this.subsetRatio = subsetRatio;
		this.random = random;
		this.useHeuristicRation = useHeuristicRation;
	}

	@Override
	public ExampleSet preprocess(ExampleSet inputSet) {
		ExampleSet exampleSet = (ExampleSet) inputSet.clone();

		double usedSubsetRatio = subsetRatio;
		if (useHeuristicRation) {
			double desiredNumber = Math.floor(Math.log(exampleSet.getAttributes().size()) / Math.log(2) + 1);
			usedSubsetRatio = desiredNumber / exampleSet.getAttributes().size();
		}
		Iterator<Attribute> i = exampleSet.getAttributes().iterator();
		while (i.hasNext()) {
			i.next();
			if (random.nextDouble() > usedSubsetRatio) {
				i.remove();
			}
		}

		// ensure that at least one attribute is left
		if (exampleSet.getAttributes().size() == 0) {
			int index = random.nextInt(inputSet.getAttributes().size());
			int counter = 0;
			for (Attribute attribute : inputSet.getAttributes()) {
				if (counter == index) {
					exampleSet.getAttributes().addRegular(attribute);
					break;
				}
				counter++;
			}
		}

		return exampleSet;
	}
}
