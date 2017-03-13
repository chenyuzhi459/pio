package io.sugo.pio.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class ParameterTypeLong extends ParameterTypeNumber {
    @JsonProperty
    private long defaultValue = -1;

    @JsonProperty
    private long min = Long.MIN_VALUE;

    @JsonProperty
    private long max = Long.MAX_VALUE;

    @JsonProperty
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

    @Override
    public boolean isNumerical() {
        return true;
    }

    @Override
    public String getRange() {
        String range = "Long; ";
        if (min == -Long.MAX_VALUE) {
            range += "-\u221E";
        } else {
            range += min;
        }
        range += "-";
        if (max == Long.MAX_VALUE) {
            range += "+\u221E";
        } else {
            range += max;
        }
        if (!noDefault) {
            range += "; default: " + getStringRepresentation(defaultValue);
        }
        return range;
    }

    public String getStringRepresentation(long defaultValue2) {
        String valueString = defaultValue2 + "";
        if (defaultValue2 == Long.MAX_VALUE) {
            valueString = "+\u221E";
        } else if (defaultValue2 == Long.MIN_VALUE) {
            valueString = "-\u221E";
        }
        return valueString;
    }
}
