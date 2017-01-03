package io.sugo.pio.spark.connections;

import io.sugo.pio.parameter.ParameterTypeSingle;

/**
 */
public class ParameterTypeHadoopConnection extends ParameterTypeSingle {
    public ParameterTypeHadoopConnection(String key, String description) {
        super(key, description);
    }

    @Override
    public Object getDefaultValue() {
        return null;
    }

    @Override
    public void setDefaultValue(Object defaultValue) {

    }
}
