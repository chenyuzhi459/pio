package io.sugo.pio.operator.error;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserError;

/**
 * UserError that indicates an unsupported model application parameter (fixed message 204).
 *
 * @author Thilo Kamradt
 */
public class UnsupportedApplicationParameterError extends UserError {

	private static final long serialVersionUID = 1L;

	public UnsupportedApplicationParameterError(Operator operator, String modelName, String parameterName) {
		super(operator, "pio.error.model_not_support_param", modelName, parameterName);
	}
}
