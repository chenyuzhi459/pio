package io.sugo.pio.parameter;

import io.sugo.pio.operator.Operator;

/**
 * This exception will be thrown if a non-optional parameter has no default value and was not
 * defined by the user.
 *
 * @author Ingo Mierswa
 */
public class UndefinedParameterError extends ParameterError {

	private static final long serialVersionUID = -2861031839668411515L;

	/** Creates a new UndefinedParameterError. */
	public UndefinedParameterError(String key) {
		super(null, 205, key, "");
	}

	public UndefinedParameterError(String key, String additionalMessage) {
		super(null, 205, key, additionalMessage);
	}

	public UndefinedParameterError(String key, Operator operator) {
		super(operator, 217, key, "");
	}

	public UndefinedParameterError(String key, Operator operator, String additionalMessage) {
		super(operator, 217, key, additionalMessage);
	}

	public UndefinedParameterError(Operator operator, String code, String additionalText) {
		super(operator, code, additionalText);
	}

	public UndefinedParameterError(Operator operator, int code, String parameterKey, Object... arguments) {
		super(operator, code, parameterKey, arguments);
	}

}
