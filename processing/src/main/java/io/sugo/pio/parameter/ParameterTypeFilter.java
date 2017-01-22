package io.sugo.pio.parameter;


import io.sugo.pio.ports.InputPort;

/**
 * This is the parameter type that can be used to define any number of filters which can be applied
 * on an example set to only show matching examples.
 * 
 * @author Marco Boeck
 * 
 */
public class ParameterTypeFilter extends ParameterType {

	private static final long serialVersionUID = 7719440206276258005L;

	private InputPort inPort;

	/**
	 * Creates a new {@link ParameterTypeFilter} instance.
	 * 
	 * @param key
	 * @param description
	 * @param inPort
	 * @param optional
	 */
	public ParameterTypeFilter(final String key, String description, InputPort inPort, boolean optional) {
		super(key, description);

		setOptional(optional);
		this.inPort = inPort;
	}

	/**
	 * Returns the {@link InputPort} where the data to apply the filter on is connected to.
	 * 
	 * @return
	 */
	public InputPort getInputPort() {
		return this.inPort;
	}


	@Override
	public String getRange() {
		return null;
	}

	@Override
	public Object getDefaultValue() {
		return null;
	}

	@Override
	public void setDefaultValue(Object defaultValue) {}

	@Override
	public boolean isNumerical() {
		return false;
	}

}
