package io.sugo.pio.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class ParameterTypeBoolean extends ParameterTypeSingle {

    @JsonProperty
    private boolean defaultValue = false;

    public ParameterTypeBoolean(String key, String description, boolean defaultValue) {
        super(key, description);
        this.defaultValue = defaultValue;
    }

    public ParameterTypeBoolean(String key, String description, boolean defaultValue, boolean expert) {
        this(key, description, defaultValue);
        setExpert(expert);
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = (Boolean)defaultValue;
    }

    @Override
    public boolean isNumerical() {
        return false;
    }

    @Override
    public String getRange() {
        return "boolean; default: " + defaultValue;
    }
}
