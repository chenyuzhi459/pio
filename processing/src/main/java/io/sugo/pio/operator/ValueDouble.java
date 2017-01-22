package io.sugo.pio.operator;

/**
 * The super class for loggable double values.
 * 
 * @author Ingo Mierswa
 */
public abstract class ValueDouble extends Value {

	/**
	 * Creates a new Value object with the given key as name and the given description. This value
	 * will be documented.
	 */
	public ValueDouble(String key, String description) {
		super(key, description);
	}

	/** Creates a new Value object. */
	public ValueDouble(String key, String description, boolean documented) {
		super(key, description, documented);
	}

	/** Returns the double value which should be logged. */
	public abstract double getDoubleValue();

	@Override
	public final boolean isNominal() {
		return false;
	}

	@Override
	public final Object getValue() {
		return getDoubleValue();
	}
}
