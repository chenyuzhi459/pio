package io.sugo.pio.parameter;


import io.sugo.pio.ports.InputPort;
import io.sugo.pio.tools.Ontology;

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

    public ParameterTypeAttributes(final String key, String description, InputPort inPort, boolean optional, boolean expert) {
        this(key, description, inPort, optional);
    }
}
