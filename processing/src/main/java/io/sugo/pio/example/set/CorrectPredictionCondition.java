package io.sugo.pio.example.set;


import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;

/**
 * This subclass of {@link Condition} serves to accept all examples which are correctly predicted.
 * 
 * @author Ingo Mierswa ingomierswa Exp $
 */
public class CorrectPredictionCondition implements Condition {

	private static final long serialVersionUID = -2971139314612252926L;

	/** Creates a new condition. */
	public CorrectPredictionCondition() {}

	/**
	 * Throws an exception since this condition does not support parameter string.
	 */
	public CorrectPredictionCondition(ExampleSet exampleSet, String parameterString) {
		if (exampleSet.getAttributes().getLabel() == null) {
			throw new IllegalArgumentException("CorrectPredictionCondition needs an example set with label attribute!");
		}
		if (exampleSet.getAttributes().getPredictedLabel() == null) {
			throw new IllegalArgumentException(
					"CorrectPredictionCondition needs an example set with predicted label attribute!");
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

	/** Returns true if the example is correctly predicted. */
	@Override
	public boolean conditionOk(Example example) {
		return example.equalValue(example.getAttributes().getLabel(), example.getAttributes().getPredictedLabel());
	}
}
