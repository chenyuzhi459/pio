package io.sugo.pio.parameter;

import java.util.Collection;


/**
 * A parameter type for regular expressions.
 * 
 * @author Tobias Malbrecht
 */
public class ParameterTypeRegexp extends ParameterTypeString {

	private static final long serialVersionUID = -4177652183651031337L;

	public ParameterTypeRegexp(final String key, String description) {
		this(key, description, true);
	}

	public ParameterTypeRegexp(final String key, String description, boolean optional) {
		super(key, description, optional);
	}

	public ParameterTypeRegexp(final String key, String description, String defaultValue) {
		super(key, description, defaultValue);
	}

	public Collection<String> getPreviewList() {
		return null;
	}

}
