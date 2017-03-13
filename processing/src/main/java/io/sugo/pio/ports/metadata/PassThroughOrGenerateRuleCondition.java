package io.sugo.pio.ports.metadata;

/**
 * This class allows subclasses to make additional checks if
 * 
 */
public interface PassThroughOrGenerateRuleCondition {

	/**
	 * This method checks if the condition is full filled. If not, errors might be registered using
	 * registerErrors()
	 * 
	 * @return
	 */
	public boolean conditionFullfilled();

	/**
	 * This method is to register errors if the condition is not fulfilled
	 */
	public void registerErrors();

}
