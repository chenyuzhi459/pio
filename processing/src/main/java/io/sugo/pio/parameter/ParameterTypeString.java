package io.sugo.pio.parameter;

/**
 */
public class ParameterTypeString extends ParameterType {
    private String defaultValue = null;

    public ParameterTypeString(String key, String description, String defaultValue) {
        super(key, description);
        this.defaultValue = defaultValue;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = (String)defaultValue;
    }
}