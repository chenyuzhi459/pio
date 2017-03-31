package io.sugo.pio.operator.learner.tree;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * The abstract super class for all split conditions.
 * 
 */
public abstract class AbstractSplitCondition implements SplitCondition {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6990047305990853177L;
	@JsonProperty
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
