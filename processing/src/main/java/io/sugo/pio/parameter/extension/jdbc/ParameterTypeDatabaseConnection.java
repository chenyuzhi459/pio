package io.sugo.pio.parameter.extension.jdbc;


import io.sugo.pio.parameter.ParameterTypeSingle;
import org.w3c.dom.Element;

public class ParameterTypeDatabaseConnection extends ParameterTypeSingle {
    private static final long serialVersionUID = 5747692587025691591L;

    public ParameterTypeDatabaseConnection(String key, String description, boolean expert) {
        this(key, description);
//        this.setExpert(expert);
    }

    public ParameterTypeDatabaseConnection(String key, String description) {
        super(key, description);
    }

    public boolean isNumerical() {
        return false;
    }

    public Object getDefaultValue() {
        return null;
    }

    public String getRange() {
        return null;
    }

    public void setDefaultValue(Object defaultValue) {
    }

    protected void writeDefinitionToXML(Element typeElement) {
    }
}
