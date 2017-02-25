package io.sugo.pio.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class ParameterTypeString extends ParameterType {
    @JsonProperty
    private String defaultValue = null;

    public ParameterTypeString(String key, String description, String defaultValue) {
        super(key, description);
        this.defaultValue = defaultValue;
    }

    public ParameterTypeString(String key, String description, boolean optional) {
        super(key, description);
        this.defaultValue = null;
        setOptional(optional);
    }

    public ParameterTypeString(String key, String description) {
        this(key, description, true);
    }

    public ParameterTypeString(String key, String description, boolean optional, boolean expert) {
        this(key, description, optional);
        setExpert(expert);
    }

    public ParameterTypeString(String key, String description, String defaultValue, boolean expert) {
        this(key, description, defaultValue);
        setExpert(expert);
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = (String)defaultValue;
    }

    /** Returns false. */
    @Override
    public boolean isNumerical() {
        return false;
    }

    @Override
    public String getRange() {
        return "string" + (defaultValue != null ? "; default: '" + defaultValue + "'" : "");
    }

}
