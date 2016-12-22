package io.sugo.pio.parameter;

/**
 */
public class ParameterTypeBoolean extends ParameterTypeSingle {
    private boolean defaultValue = false;

    public ParameterTypeBoolean(String key, String description, boolean defaultValue) {
        super(key, description);
        this.defaultValue = defaultValue;
    }

    @Override
    public Object getDefaultValue() {
        return defaultValue;
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = (Boolean)defaultValue;
    }
}
