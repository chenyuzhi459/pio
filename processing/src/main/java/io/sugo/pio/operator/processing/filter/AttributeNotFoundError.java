package io.sugo.pio.operator.processing.filter;


import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserError;

/**
 * This exception will be thrown if an attribute was specified in the parameters of an operator but
 * was not found in the data.
 *
 *
 */
public class AttributeNotFoundError extends UserError {

	/** if the attribute could neither be found in regular nor in special attributes */
	public static final int ATTRIBUTE_NOT_FOUND = 160;

	/** if the attribute could not be found in regular attributes */
	public static final int ATTRIBUTE_NOT_FOUND_IN_REGULAR = 164;

	private static final long serialVersionUID = 4107157631726397970L;

	private String key;
	private String attributeName;

	/**
	 * Throw if the parameter of an operator specifies an attribute which cannot be found in the
	 * input data.
	 *
	 * @param operator
	 *            the operator in question
	 * @param key
	 *            the parameter key for which the error occured
	 * @param attributeName
	 *            the name of the attribute
	 */
	public AttributeNotFoundError(Operator operator, String key, String attributeName) {
		this(operator, ATTRIBUTE_NOT_FOUND, key, attributeName);
	}

	/**
	 * Throw if the parameter of an operator specifies an attribute which cannot be found in the
	 * input data.
	 *
	 * @param operator
	 *            the operator in question
	 * @param code
	 *            the error code, see class constants
	 * @param key
	 *            the parameter key for which the error occured
	 * @param attributeName
	 *            the name of the attribute
	 */
	public AttributeNotFoundError(Operator operator, int code, String key, String attributeName) {
		super(operator, code, attributeName);
		if (attributeName == null) {
			this.attributeName = "";
		} else {
			this.attributeName = attributeName;
		}
		this.key = key;
	}

	/**
	 * @return the key of the parameter which caused the error. Can be {@code null}
	 */
	public String getKey() {
		return key;
	}

	/**
	 * @return the name of the attribute which was not found
	 */
	public String getAttributeName() {
		return attributeName;
	}

}
