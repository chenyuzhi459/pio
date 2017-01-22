package io.sugo.pio.example.set;


import io.sugo.pio.example.Example;
import io.sugo.pio.operator.tools.ExpressionEvaluationException;

import java.io.Serializable;

/**
 * Objects implementing this interface are used by
 * {@link io.sugo.pio.example.set.ConditionedExampleSet}s, a special sub class of
 * {@link io.sugo.pio.example.ExampleSet} that skips all examples that do not fulfill this
 * condition. In order for the
 * {@link io.sugo.pio.example.set.ConditionedExampleSet#createCondition(String, ExampleSet, String)}
 * factory method to be able to create instances of an implementation of Condition, it must
 * implement a two argument constructor taking an {@link ExampleSet} and a parameter String. The
 * meaning of the parameter string is dependent on the implementation and may even be ignored,
 * although it would be nice to print a warning.
 * 
 * @author Ingo Mierswa, Simon Fischer
 */
public interface Condition extends Serializable {

	/**
	 * Should return true if the given example does fulfill this condition.
	 * 
	 * @param example
	 * @return
	 * @throws ExpressionEvaluationException
	 *             if the condition cannot be evaluated
	 */
	public boolean conditionOk(Example example) throws ExpressionEvaluationException;

	/**
	 * Returns a duplicate of this condition. Subclasses which cannot dynamically changed can also
	 * return the same object.
	 * 
	 * @deprecated Conditions should not be able to be changed dynamically and hence there is no
	 *             need for a copy
	 */
	@Deprecated
	public Condition duplicate();

}
