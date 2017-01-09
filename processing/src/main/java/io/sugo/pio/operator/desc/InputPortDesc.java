package io.sugo.pio.operator.desc;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class InputPortDesc extends CustomParameterDesc {
    @JsonProperty
    private static String portType = "InputPort";

    public InputPortDesc(String name, String description) {
        super(name, description);
    }

    public InputPortDesc(String name, String description, List<Description> params) {
        super(name, description, params);
    }

    public String getPortType() {
        return portType;
    }

    public static InputPortDesc create(String name) {
        return new InputPortDesc(name, portType);
    }

    public static InputPortDesc create(String name, String description, List<Description> params) {
        return new InputPortDesc(name, description, params);
    }
}
