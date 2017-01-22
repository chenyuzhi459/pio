package io.sugo.pio.example.set;


import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;

/**
 * This subclass of {@link Condition} serves to exclude examples with unknown labels from an example
 * set.
 * 
 * @author Ingo Mierswa ingomierswa Exp $
 */
public class NoMissingLabelsCondition implements Condition {

	private static final long serialVersionUID = 8047504208389222350L;

	/**
	 * Throws an exception since a parameter string is not allowed for this condition.
	 */
	public NoMissingLabelsCondition(ExampleSet exampleSet, String parameterString) {}

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

	/** Returns true if the label was defined. */
	@Override
	public boolean conditionOk(Example example) {
		if (Double.isNaN(example.getValue(example.getAttributes().getLabel()))) {
			return false;
		} else {
			return true;
		}
	}
}
