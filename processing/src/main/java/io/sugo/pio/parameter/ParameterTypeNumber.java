package io.sugo.pio.parameter;

/**
 */
public abstract class ParameterTypeNumber extends ParameterTypeSingle {

    public ParameterTypeNumber(String key, String description) {
        super(key, description);
    }

    public abstract double getMinValue();

    public abstract double getMaxValue();
}
