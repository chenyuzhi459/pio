package io.sugo.pio.operator.desc;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by root on 17-1-5.
 */
public class ParameterDesc implements Description {
    @JsonProperty
    private String name;
    @JsonProperty
    private String description;
    @JsonProperty
    private ParamType type;
    @JsonProperty
    private String defaultValue;

    public ParameterDesc(String name, String description, ParamType type) {
        this.name = name;
        this.description = description;
        this.type = type;
    }

    public ParameterDesc(String name, String description, ParamType type, String defaultValue) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.defaultValue = defaultValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ParamType getType() {
        return type;
    }

    public void setType(ParamType type) {
        this.type = type;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public static ParameterDesc create(String name, String description, ParamType type) {
        return new ParameterDesc(name, description, type);
    }

    public static ParameterDesc create(String name, String description, ParamType type, String defaultValue) {
        return new ParameterDesc(name, description, type, defaultValue);
    }

    public static ParameterDesc createInt(String name, String description) {
        return new ParameterDesc(name, description, ParamType.INTEGER);
    }

    public static ParameterDesc createString(String name, String description) {
        return new ParameterDesc(name, description, ParamType.STRING);
    }

    public static ParameterDesc createDouble(String name, String description) {
        return new ParameterDesc(name, description, ParamType.DOUBLE);
    }

    public static ParameterDesc createCustom(String name, String description) {
        return new ParameterDesc(name, description, ParamType.CUSTOM);
    }
}
