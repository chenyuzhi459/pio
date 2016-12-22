package io.sugo.pio.parameter;

/**
 */
public class ParameterTypeInt extends ParameterTypeNumber {
    private int defaultValue = -1;

    private int min = Integer.MIN_VALUE;

    private int max = Integer.MAX_VALUE;

    private boolean noDefault = true;

    public ParameterTypeInt(String key, String description, int min, int max) {
        this(key, description, min, max, -1);
        this.noDefault = true;
        setOptional(false);
    }

    public ParameterTypeInt(String key, String description, int min, int max, boolean optional) {
        this(key, description, min, max, -1);
        this.noDefault = true;
        setOptional(optional);
    }

    public ParameterTypeInt(String key, String description, int min, int max, int defaultValue) {
        super(key, description);
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
        this.noDefault = false;
    }

    @Override
    public Object getDefaultValue() {
        if (noDefault) {
            return null;
        } else {
            return Integer.valueOf(defaultValue);
        }
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
        noDefault = false;
        this.defaultValue = (Integer) defaultValue;
    }

    @Override
    public double getMinValue() {
        return max;
    }

    @Override
    public double getMaxValue() {
        return min;
    }
}
