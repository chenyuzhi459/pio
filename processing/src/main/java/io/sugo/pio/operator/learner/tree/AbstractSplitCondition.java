package io.sugo.pio.operator.learner.tree;

/**
 * The abstract super class for all split conditions.
 * 
 * @author Ingo Mierswa
 */
public abstract class AbstractSplitCondition implements SplitCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6990047305990853177L;
	private String attributeName;

	public AbstractSplitCondition(String attributeName) {
		this.attributeName = attributeName;
	}

	@Override
	public String getAttributeName() {
		return attributeName;
	}

	@Override
	public String toString() {
		return attributeName + " " + getRelation() + " " + getValueString();
	}
}