package io.sugo.pio.example.set;


import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;

/**
 * This subclass of {@link Condition} serves to accept all examples which are wrongly predicted.
 * 
 * @author Ingo Mierswa ingomierswa Exp $
 */
public class WrongPredictionCondition implements Condition {

	private static final long serialVersionUID = -3254098600455281034L;

	/** Creates a new condition. */
	public WrongPredictionCondition() {}

	/**
	 * Throws an exception since this condition does not support parameter string.
	 */
	public WrongPredictionCondition(ExampleSet exampleSet, String parameterString) {
		if (exampleSet.getAttributes().getLabel() == null) {
			throw new IllegalArgumentException("FalsePredictionCondition needs an example set with label attribute!");
		}
		if (exampleSet.getAttributes().getPredictedLabel() == null) {
			throw new IllegalArgumentException(
					"FalsePredictionCondition needs an example set with predicted label attribute!");
		}
	}

	/**
	 * Since the condition cannot be altered after creation we can just return the condition object
	 * itself.
	 * 
	 * @deprecated Conditions should not be able to be changed dynamically and hence there is no
	 *             need for a copy
	 */
	@Override
	@Deprecated
	public Condition duplicate() {
		return this;
	}

	/** Returns true if the example wrongly classified. */
	@Override
	public boolean conditionOk(Example example) {
		return !example.equalValue(example.getAttributes().getLabel(), example.getAttributes().getPredictedLabel());
	}
}
