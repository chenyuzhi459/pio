package io.sugo.pio.operator;

/**
 */
public class InputDescription {

    /** The class of the input object. */
    private Class<?> inputType;

    /** The default value for consumation. */
    private boolean keepDefault;

    /**
     * Indicates if the operator at hand should define a parameter so that the user can decide if
     * the input should be consumed.
     */
    private boolean parameter;

    /** The parameter name. */
    private String parameterName;

    /**
     * Creates a new input description for the given class. The input object is consumed.
     */
    public InputDescription(Class<?> inputType) {
        this(inputType, false, false, null);
    }

    /**
     * Creates a new input description for the given class. The parameter keepDefault defines if the
     * input object is consumed per default.
     * <code>parameter<code> defines if the operator should provide a user parameter. This parameter utilizes the given name.
     */
    public InputDescription(Class<?> inputType, boolean keepDefault, boolean parameter, String parameterName) {
        this.inputType = inputType;
        this.keepDefault = keepDefault;
        this.parameter = parameter;
        this.parameterName = parameterName;
    }

    /** Indicates if the input should be consumed. */
    public boolean getKeepDefault() {
        return keepDefault;
    }

    /** Indicates if a user parameter should be defined. */
    public boolean showParameter() {
        return parameter;
    }

    /** Returns the name of the user parameter. */
    public String getParameterName() {
        return parameterName;
    }
}
