package io.sugo.pio.operator.learner.associations.fpgrowth;

/**
 * A stack for frequencies.
 * 
 */
public interface FrequencyStack {

	/**
	 * Increases the frequency stored on stackHeight level of stack by value, if stackHeight is the
	 * top of stack, or stackHeight is top of stack + 1
	 * 
	 * @param stackHeight
	 *            describes the level of stack, counted from bottom on which the value is added
	 * @param value
	 *            is the amount added
	 */
	public void increaseFrequency(int stackHeight, int value);

	/**
	 * This method deletes the heightTH element of stack.
	 */
	public void popFrequency(int height);

	/**
	 * Returns the frequency stored on height of stack.
	 */
	public int getFrequency(int height);
}
