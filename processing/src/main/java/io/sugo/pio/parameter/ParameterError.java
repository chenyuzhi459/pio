package io.sugo.pio.parameter;


import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserError;

/**
 * This exception will be thrown if something is wrong with a parameter. If possible, use the more
 * specific subclasses for improved error handling in the GUI.
 *
 * @author Marco Boeck
 * @since 6.5.0
 */
public class ParameterError extends UserError {

	private static final long serialVersionUID = -7390311132493751678L;

	/** the parameter key which caused the error */
	private String key;

	public ParameterError(Operator operator, int code, String parameterkey, String additionalText) {
		this(operator, code, parameterkey, new Object[] { parameterkey, additionalText });
	}

	public ParameterError(Operator operator, int code, String parameterkey, Object... arguments) {
		super(operator, code, arguments);
		this.key = parameterkey;
	}

	public ParameterError(Operator operator, String code, String parameterkey, String additionalText) {
		this(operator, code, parameterkey, new Object[] { parameterkey, additionalText });
	}

	public ParameterError(Operator operator, String code, String parameterkey, Object... arguments) {
		super(operator, code, arguments);
		this.key = parameterkey;
	}

	/**
	 * @return the key of the parameter which caused the error. Can be {@code null} in very rare
	 *         cases
	 */
	public String getKey() {
		return key;
	}
}
