package io.sugo.pio.operator.learner.tree;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.learner.tree.criterions.ColumnCriterion;

import java.util.List;


/**
 * Builds a tree, not in parallel. The example set is preprocessed before starting the procedure. In
 * each splitting step (except for the first), the attribute selection is preprocessed again. The
 * preprocessings must be non-null.
 *
 */
public class NonParallelPreprocessingTreeBuilder extends NonParallelTreeBuilder {

	private final SplitPreprocessing splitPreprocessing;

	/**
	 * Checks that the preprocessings are not null. Stores the splitPreprocessing and pipes the
	 * other parameters to the super constructor.
	 */
	public NonParallelPreprocessingTreeBuilder(Operator operator, ColumnCriterion criterion,
                                               List<ColumnTerminator> terminationCriteria, Pruner pruner, AttributePreprocessing preprocessing,
                                               boolean prePruning, int numberOfPrepruningAlternatives, int minSizeForSplit, int minLeafSize,
                                               SplitPreprocessing splitPreprocessing) {
		super(operator, criterion, terminationCriteria, pruner, preprocessing, prePruning, numberOfPrepruningAlternatives,
				minSizeForSplit, minLeafSize);
		// the two preprocessings must be non-zero
		if (preprocessing == null) {
			throw new IllegalArgumentException("preprocessing must not be null");
		}
		if (splitPreprocessing == null) {
			throw new IllegalArgumentException("splitPreprocessing must not be null");
		}
		this.splitPreprocessing = splitPreprocessing;
	}

	@Override
	protected ExampleSet preprocessExampleSet(ExampleSet exampleSet) {
		return splitPreprocessing.preprocess(exampleSet);
	}

}
