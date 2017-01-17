package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.SortedExampleSet;
import io.sugo.pio.operator.OperatorException;
import io.sugo.pio.operator.learner.tree.criterions.Criterion;
import io.sugo.pio.tools.Tools;

import java.util.Iterator;

/**
 * Calculates the best split point for numerical attributes according to a given criterion.
 * 
 * @author Ingo Mierswa
 */
public class NumericalSplitter {

	private Criterion criterion;

	public NumericalSplitter(Criterion criterion) {
		this.criterion = criterion;
	}

	public double getBestSplit(ExampleSet inputSet, Attribute attribute) throws OperatorException {
		SortedExampleSet exampleSet = new SortedExampleSet((ExampleSet) inputSet.clone(), attribute,
				SortedExampleSet.INCREASING);
		// Attribute labelAttribute = exampleSet.getAttributes().getLabel(); // see bug report 952
		// double oldLabel = Double.NaN; // see bug report 952
		double bestSplit = Double.NaN;
		double lastValue = Double.NaN;
		double bestSplitBenefit = Double.NEGATIVE_INFINITY;

		Example lastExample = null;
		if (this.criterion.supportsIncrementalCalculation()) {
			this.criterion.startIncrementalCalculation(exampleSet);
		}

		Iterator<Example> exampleIterator = exampleSet.iterator();
		while (exampleIterator.hasNext()) {
			Example e = exampleIterator.next();
			// boolean isLast = !(exampleIterator.hasNext()); // see bug report 952
			double currentValue = e.getValue(attribute);

			// double label = e.getValue(labelAttribute); // see bug report 952
			if (this.criterion.supportsIncrementalCalculation()) {
				if (lastExample != null) {
					this.criterion.swapExample(lastExample);
				}
				lastExample = e;
				// if ((Double.isNaN(oldLabel)) || (oldLabel != label) || isLast) { // see bug
				// report 952
				// if ((Double.isNaN(oldLabel)) || (oldLabel != label)) { // see bug report 952
				if (!Tools.isEqual(currentValue, lastValue)) {
					double benefit = this.criterion.getIncrementalBenefit();

					if (benefit > bestSplitBenefit) {
						bestSplitBenefit = benefit;
						bestSplit = (lastValue + currentValue) / 2.0d;
					}
					// oldLabel = label; // see bug report 952
				}
				// } // see bug report 952
				// } else if ((Double.isNaN(oldLabel)) || (oldLabel != label)) { // see bug report
				// 952
			} else {
				if (!Tools.isEqual(currentValue, lastValue)) {
					double splitValue = (lastValue + currentValue) / 2.0d;
					double benefit = this.criterion.getNumericalBenefit(exampleSet, attribute, splitValue);
					if (benefit > bestSplitBenefit) {
						bestSplitBenefit = benefit;
						bestSplit = splitValue;
					}
					// oldLabel = label; // see bug report 952
				}
			}

			lastValue = currentValue;
		}
		return bestSplit;
	}
}
