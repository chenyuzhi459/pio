package io.sugo.pio.parameter;

import io.sugo.pio.ports.InputPort;
import io.sugo.pio.tools.Ontology;

/**
 * A parameter type for selecting several attributes. This is merely a copy of the
 * {@link ParameterTypeAttribute}, since it already comes with all needed functions. But we register
 * a different CellRenderer for this class.
 * 
 * @author Tobias Malbrecht, Sebastian Land
 */
public class ParameterTypeAttributes extends ParameterTypeAttribute {

	private static final long serialVersionUID = -4177652183651031337L;

	public static final String ATTRIBUTE_SEPARATOR_CHARACTER = "|";

	public static final String ATTRIBUTE_SEPARATOR_REGEX = "\\|";

	public ParameterTypeAttributes(final String key, String description, InputPort inPort) {
		this(key, description, inPort, true, Ontology.ATTRIBUTE_VALUE);
	}

	public ParameterTypeAttributes(final String key, String description, InputPort inPort, int... valueTypes) {
		this(key, description, inPort, true, valueTypes);
	}

	public ParameterTypeAttributes(final String key, String description, InputPort inPort, boolean optional) {
		this(key, description, inPort, optional, Ontology.ATTRIBUTE_VALUE);
	}

	public ParameterTypeAttributes(final String key, String description, InputPort inPort, boolean optional,
								   int... valueTypes) {
		super(key, description, inPort, optional, valueTypes);
	}
}
