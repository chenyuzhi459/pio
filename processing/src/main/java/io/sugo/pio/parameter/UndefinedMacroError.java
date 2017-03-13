package io.sugo.pio.parameter;


import io.sugo.pio.operator.Operator;

/**
 * This Exception will be thrown if an Macro was not defined.
 */
public class UndefinedMacroError extends UndefinedParameterError {

    private static final long serialVersionUID = 1547250316954515775L;

    /**
     * The default constructor for missing macro errors with error code 227.
     *
     * @param macroKey the key of the missing macro
     */
    public UndefinedMacroError(String parameterKey, String macroKey) {
        super(null, "pio.error.macro_undefined", parameterKey, macroKey);
    }

    /**
     * @param operator       the executing Operator which performs the action or null
     * @param key            errorID of the UserErrorMessage which should be shown
     * @param additionalText text to paste in the UserErrorMessage
     */
    public UndefinedMacroError(Operator operator, String key, String additionalText) {
        super(operator, key, additionalText);
    }

}
