package io.sugo.pio.parameter;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 */
public class ParameterTypeDouble extends ParameterTypeNumber {
    @JsonProperty
    private double defaultValue = Double.NaN;

    @JsonProperty
    private double min = Double.NEGATIVE_INFINITY;

    @JsonProperty
    private double max = Double.POSITIVE_INFINITY;

    @JsonProperty
    private boolean noDefault = true;

    public ParameterTypeDouble(String key, String description, double min, double max) {
        this(key, description, min, max, Double.NaN);
        this.noDefault = true;
    }

    public ParameterTypeDouble(String key, String description, double min, double max, boolean optional) {
        this(key, description, min, max, Double.NaN);
        this.noDefault = true;
        setOptional(optional);
    }

    public ParameterTypeDouble(String key, String description, double min, double max, double defaultValue) {
        super(key, description);
        this.defaultValue = defaultValue;
        this.min = min;
        this.max = max;
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
        if (Double.isNaN(defaultValue)) {
            return null;
        } else {
            return Double.valueOf(defaultValue);
        }
    }

    @Override
    public void setDefaultValue(Object object) {
        this.defaultValue = (Double) object;
    }

    @Override
    public boolean isNumerical() {
        return true;
    }

    @Override
    public String getRange() {
        String range = "real; ";
        if (min == Double.NEGATIVE_INFINITY) {
            range += "-\u221E";
        } else {
            range += min;
        }
        range += "-";
        if (max == Double.POSITIVE_INFINITY) {
            range += "+\u221E";
        } else {
            range += max;
        }
        if (!noDefault) {
            range += "; default: " + defaultValue;
        }
        return range;
    }
}
