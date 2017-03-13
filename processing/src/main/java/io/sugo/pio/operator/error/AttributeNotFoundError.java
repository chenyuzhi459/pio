package io.sugo.pio.operator.error;

import io.sugo.pio.operator.Operator;
import io.sugo.pio.operator.UserError;

/**
 * This exception will be thrown if an attribute was specified in the parameters of an operator but
 * was not found in the data.
 *
 * @author Marco Boeck
 * @since 6.5.0
 *
 */
public class AttributeNotFoundError extends UserError {

	private static final long serialVersionUID = 4107157631726397970L;

	/** if the attribute could neither be found in regular nor in special attributes */
	public static final String ATTRIBUTE_NOT_FOUND = "pio.error.attribute_not_exist";

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
	 * @param errorId
	 *            the error id
	 * @param key
	 *            the parameter key for which the error occured
	 * @param attributeName
	 *            the name of the attribute
	 */
	public AttributeNotFoundError(Operator operator, String errorId, String key, String attributeName) {
		super(operator, errorId, attributeName);
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
