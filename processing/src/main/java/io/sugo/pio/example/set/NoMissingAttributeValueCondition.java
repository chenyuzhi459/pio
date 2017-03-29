package io.sugo.pio.example.set;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;


/**
 * This subclass of {@link Condition} serves to excludes all examples containing missing values
 * within specified attributes from an example set. The parameters might be specified using a
 * regular expression as parameter string
 * 
 */
public class NoMissingAttributeValueCondition implements Condition {

	private static final long serialVersionUID = -6043772701857922762L;

	private Collection<Attribute> checkedAttributes = new LinkedList<Attribute>();

	public NoMissingAttributeValueCondition(ExampleSet exampleSet, String parameterString) {
		Iterator<Attribute> iterator = exampleSet.getAttributes().allAttributes();
		while (iterator.hasNext()) {
			Attribute attribute = iterator.next();
			if (attribute.getName().matches(parameterString)) {
				checkedAttributes.add(attribute);
			}
		}
	}

	/** Returns true if the example does not contain missing values within regarded attributes. */
	@Override
	public boolean conditionOk(Example example) {
		boolean isOk = true;
		for (Attribute attribute : checkedAttributes) {
			isOk &= !Double.isNaN(example.getValue(attribute));
		}
		return isOk;
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

}
