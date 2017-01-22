package io.sugo.pio.example.set;


import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;

/**
 * This subclass of {@link Condition} serves to exclude examples with known labels from an example
 * set.
 * 
 */
public class MissingLabelsCondition implements Condition {

	private static final long serialVersionUID = 6559275828082706521L;

	/**
	 * Throws an exception since a parameter string is not allowed for this condition.
	 */
	public MissingLabelsCondition(ExampleSet exampleSet, String parameterString) {}

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

	/** Returns true if the label was not defined. */
	@Override
	public boolean conditionOk(Example example) {
		if (Double.isNaN(example.getValue(example.getAttributes().getLabel()))) {
			return true;
		} else {
			return false;
		}
	}
}
