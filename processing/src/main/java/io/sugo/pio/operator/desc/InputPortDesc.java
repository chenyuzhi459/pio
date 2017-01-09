package io.sugo.pio.operator.desc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class InputPortDesc extends CustomParameterDesc {
    private final static String PORT_TYPE = "InputPort";
    @JsonProperty
    private final String portType = PORT_TYPE;

    public InputPortDesc(String name) {
        super(name, PORT_TYPE);
    }

    public InputPortDesc(String name, String description, List<Description> params) {
        super(name, description, params);
    }

    public String getPortType() {
        return portType;
    }

    public static InputPortDesc create(String name) {
        return new InputPortDesc(name);
    }

    public static InputPortDesc create(String name, String description, List<Description> params) {
        return new InputPortDesc(name, description, params);
    }
}
