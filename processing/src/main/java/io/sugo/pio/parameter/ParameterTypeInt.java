package io.sugo.pio.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class ParameterTypeInt extends ParameterTypeNumber {
    @JsonProperty
    private int defaultValue = -1;
    @JsonProperty
    private int min = Integer.MIN_VALUE;
    @JsonProperty
    private int max = Integer.MAX_VALUE;
    @JsonProperty
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

    public ParameterTypeInt(String key, String description, int min, int max, int defaultValue, boolean expert) {
        this(key, description, min, max, defaultValue);
        setExpert(expert);
    }

    public ParameterTypeInt(String key, String description, int min, int max, int defaultValue, boolean expert, boolean hidden) {
        this(key, description, min, max, defaultValue);
        setExpert(expert);
        setHidden(hidden);
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

    /** Returns true. */
    @Override
    public boolean isNumerical() {
        return true;
    }

    @Override
    public String getRange() {
        String range = "integer; ";
        if (min == -Integer.MAX_VALUE) {
            range += "-\u221E";
        } else {
            range += min;
        }
        range += "-";
        if (max == Integer.MAX_VALUE) {
            range += "+\u221E";
        } else {
            range += max;
        }
        if (!noDefault) {
            range += "; default: " + getStringRepresentation(defaultValue);
        }
        return range;
    }

    public String getStringRepresentation(int value) {
        String valueString = value + "";
        if (value == Integer.MAX_VALUE) {
            valueString = "+\u221E";
        } else if (value == Integer.MIN_VALUE) {
            valueString = "-\u221E";
        }
        return valueString;
    }

}
