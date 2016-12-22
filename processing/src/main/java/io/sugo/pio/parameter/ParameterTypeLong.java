package io.sugo.pio.parameter;

/**
 */
public class ParameterTypeLong extends ParameterTypeNumber {
    private long defaultValue = -1;

    private long min = Long.MIN_VALUE;

    private long max = Long.MAX_VALUE;

    private boolean noDefault = true;

    public ParameterTypeLong(String key, String description, long min, long max) {
        this(key, description, min, max, -1);
        this.noDefault = true;
        setOptional(false);
    }

    public ParameterTypeLong(String key, String description, long min, long max, boolean optional) {
        this(key, description, min, max, -1);
        this.noDefault = true;
        setOptional(optional);
    }

    public ParameterTypeLong(String key, String description, long min, long max, long defaultValue) {
        super(key, description);
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
        this.noDefault = false;
    }

    @Override
    public double getMinValue() {
        return min;
    }

    @Override
    public double getMaxValue() {
        return max;
    }

    @Override
    public Object getDefaultValue() {
        if (noDefault) {
            return null;
        } else {
            return Long.valueOf(defaultValue);
        }
    }

    @Override
    public void setDefaultValue(Object defaultValue) {
        noDefault = false;
        this.defaultValue = (Long) defaultValue;
    }
}
