package io.sugo.pio.parameter;

import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.Port;
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

    //	public static final String ATTRIBUTE_SEPARATOR_CHARACTER = "|";
    public static final String ATTRIBUTE_SEPARATOR_CHARACTER = ";";

    //	public static final String ATTRIBUTE_SEPARATOR_REGEX = "\\|";
    public static final String ATTRIBUTE_SEPARATOR_REGEX = ";";

    public ParameterTypeAttributes(final String key, String description, Port port) {
        this(key, description, port, true, Ontology.ATTRIBUTE_VALUE);
    }

    public ParameterTypeAttributes(final String key, String description, Port port, int... valueTypes) {
        this(key, description, port, true, valueTypes);
    }

    public ParameterTypeAttributes(final String key, String description, Port port, boolean optional) {
        this(key, description, port, optional, Ontology.ATTRIBUTE_VALUE);
    }

    public ParameterTypeAttributes(final String key, String description, Port port, boolean optional,
                                   int... valueTypes) {
        super(key, description, port, optional, valueTypes);
    }

    public ParameterTypeAttributes(final String key, String description, MetaDataProvider metaDataProvider, boolean optional,
                                   int... valueTypes) {
        super(key, description, metaDataProvider, optional, valueTypes);
    }
}
