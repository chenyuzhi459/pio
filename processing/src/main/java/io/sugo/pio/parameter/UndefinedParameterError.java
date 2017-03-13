package io.sugo.pio.parameter;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.error.ParameterError;

/**
 * This exception will be thrown if a non-optional parameter has no default value and was not
 * defined by the user.
 *
 */
public class UndefinedParameterError extends ParameterError {

    private static final long serialVersionUID = -2861031839668411515L;

    /**
     * Creates a new UndefinedParameterError.
     */
    public UndefinedParameterError(String key) {
        super(null, "pio.error.parameter_missing_1", key, "");
    }

    public UndefinedParameterError(String key, String additionalMessage) {
        super(null, "pio.error.parameter_missing_1", key, additionalMessage);
    }

    public UndefinedParameterError(String key, Operator operator) {
        super(operator, "pio.error.parameter_missing_2", key, "");
    }

    public UndefinedParameterError(String key, Operator operator, String additionalMessage) {
        super(operator, "pio.error.parameter_missing_2", key, additionalMessage);
    }

    public UndefinedParameterError(Operator operator, String code, String additionalText) {
        super(operator, code, additionalText);
    }

    public UndefinedParameterError(Operator operator, String errorId, String parameterkey, String additionalText) {
        super(operator, errorId, parameterkey, new Object[]{parameterkey, additionalText});
    }

}
