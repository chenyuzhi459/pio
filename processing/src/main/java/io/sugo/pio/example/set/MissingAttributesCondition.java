package io.sugo.pio.example.set;


import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;

/**
 * This subclass of {@link Condition} serves to excludes all examples containing no missing values
 * from an example set.
 * 
 */
public class MissingAttributesCondition implements Condition {

	private static final long serialVersionUID = 6872303452739421943L;

	/**
	 * Throws an exception since this condition does not support parameter string.
	 */
	public MissingAttributesCondition(ExampleSet exampleSet, String parameterString) {}

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

	/** Returns true if the example does not contain missing values. */
	@Override
	public boolean conditionOk(Example example) {
		for (Attribute attribute : example.getAttributes()) {
			if (Double.isNaN(example.getValue(attribute))) {
				return true;
			}
		}
		return false;
	}
}
