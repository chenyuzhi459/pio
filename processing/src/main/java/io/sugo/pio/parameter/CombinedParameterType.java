package io.sugo.pio.parameter;


/**
 * This is an abstract class for all ParameterTypes that are a combination of several other
 * {@link ParameterType}s. In fact it doesn't do anything...
 * 
 * @author Sebastian Land
 * 
 */
public abstract class CombinedParameterType extends ParameterType {

	private static final long serialVersionUID = 1674072082952288334L;

	public CombinedParameterType(String key, String description, ParameterType... types) {
		super(key, description);
	}

}
