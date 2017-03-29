package io.sugo.pio.operator.performance;

import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.OperatorException;

/**
 */
public abstract class MeasuredPerformance extends PerformanceCriterion {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4465054472762456363L;

	public MeasuredPerformance() {}

	public MeasuredPerformance(MeasuredPerformance o) {
		super(o);
	}

	/** Counts a single example, e.g. by summing up errors. */
	public abstract void countExample(Example example);

	/**
	 * Initialized the criterion. The default implementation invokes the initialization method with
	 * useExampleWeights set to true.
	 * 
	 * @deprecated Please use the other start counting method directly
	 */
	@Deprecated
	public final void startCounting(ExampleSet set) throws OperatorException {
		startCounting(set, true);
	}

	/** Initializes the criterion. The default implementation does nothing. */
	public void startCounting(ExampleSet set, boolean useExampleWeights) throws OperatorException {}

}
