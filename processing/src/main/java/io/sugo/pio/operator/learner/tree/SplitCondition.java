package io.sugo.pio.operator.learner.tree;


import io.sugo.pio.example.Example;

import java.io.Serializable;

/**
 * A condition for a split in decision tree, rules etc. Subclasses should also implement a toString
 * method.
 * 
 * @author Sebastian Land, Ingo Mierswa
 */
public interface SplitCondition extends Serializable {

	public String getAttributeName();

	public String getRelation();

	public String getValueString();

	public boolean test(Example example);

}
