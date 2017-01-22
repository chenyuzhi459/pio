package io.sugo.pio.parameter;

import java.util.Map;


/**
 * This parameter type will lead to a GUI element which can be used as initialization for a sort of
 * operator configuration wizard.
 *
 * @author Ingo Mierswa
 */
public class ParameterTypeConfiguration extends ParameterType {

    private static final long serialVersionUID = -3512071671355815277L;

    public static final String PARAMETER_DEFAULT_CONFIGURATION_NAME = "configure_operator";

    private Map<String, String> parameters = null;

    public Object[] wizardConstructionArguments;

    public ParameterTypeConfiguration(Map<String, String> parameters) {
        this(parameters, null);

    }

    public ParameterTypeConfiguration(
            Map<String, String> parameters, Object[] constructorArguments) {
        super(PARAMETER_DEFAULT_CONFIGURATION_NAME, "Configure this operator by means of a Wizard.");
        this.parameters = parameters;
        this.wizardConstructionArguments = constructorArguments;
    }

    /**
     * Returns null.
     */
    @Override
    public Object getDefaultValue() {
        return null;
    }

    /**
     * Does nothing.
     */
    @Override
    public void setDefaultValue(Object value) {
    }

    @Override
    public String getRange() {
        return null;
    }

    @Override
    public boolean isNumerical() {
        return false;
    }
}
