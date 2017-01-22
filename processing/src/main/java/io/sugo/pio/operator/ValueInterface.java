package io.sugo.pio.operator;

/**
 * The interface for values which can logged and plotted during process definitions. Operators
 * should define their values in their constructor.
 * 
 */
public interface ValueInterface {

	/** Returns a human readable description. */
	public String getDescription();

	/** Returns the key. */
	public String getKey();

	/** Returns true if this value should be documented. */
	public boolean isDocumented();

	/**
	 * Returns the current value which can be logged by the process log operator.
	 */
	public Object getValue();

	/** Returns true if the value is nominal. */
	public boolean isNominal();

}
