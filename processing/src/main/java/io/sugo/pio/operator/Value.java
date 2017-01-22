package io.sugo.pio.operator;

/**
 * A value contains a key and a description. The current value can be asked by the process log
 * operator.
 * 
 */
public abstract class Value implements ValueInterface {

	/** The key which can be asked by the process log operator. */
	private String key;

	/** The human readable description of this value. */
	private String description;

	/** Indicates if this value should be documented. */
	private boolean documented = true;

	/**
	 * Creates a new Value object with the given key as name and the given description. This value
	 * will be documented.
	 */
	public Value(String key, String description) {
		this(key, description, true);
	}

	/** Creates a new Value object. */
	public Value(String key, String description, boolean documented) {
		this.key = key;
		this.description = description;
		this.documented = documented;
	}

	/** Returns a human readable description. */
	@Override
	public String getDescription() {
		return description;
	}

	/** Returns the key. */
	@Override
	public String getKey() {
		return key;
	}

	/** Returns true if this value should be documented. */
	@Override
	public boolean isDocumented() {
		return documented;
	}
}
