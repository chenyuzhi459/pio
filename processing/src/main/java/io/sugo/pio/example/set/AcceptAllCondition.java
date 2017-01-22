package io.sugo.pio.example.set;


import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;

/**
 * This subclass of {@link Condition} serves to accept all examples.
 * 
 * @author Ingo Mierswa Exp $
 */
public class AcceptAllCondition implements Condition {

	private static final long serialVersionUID = 9217842736819037165L;

	/** Creates a new condition. */
	public AcceptAllCondition() {}

	/**
	 * Throws an exception since this condition does not support parameter string.
	 */
	public AcceptAllCondition(ExampleSet exampleSet, String parameterString) {}

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

	/** Returns true. */
	@Override
	public boolean conditionOk(Example example) {
		return true;
	}
}
