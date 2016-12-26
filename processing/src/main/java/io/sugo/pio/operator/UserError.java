/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 *
 * Complete list of developers available at our web site:
 *
 * http://rapidminer.com
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.sugo.pio.operator;

import java.text.MessageFormat;



public class UserError extends OperatorException implements NoBugError {

	private static final long serialVersionUID = -8441036860570180869L;

	private static final MessageFormat formatter = new MessageFormat("");

	private String errorIdentifier = null;

	private final int code;

	private transient Operator operator;

	private final Object[] arguments;

	/**
	 * Creates a new UserError.
	 *
	 * @param operator
	 *            The {@link Operator} in which the exception occured.
	 * @param cause
	 *            The exception that caused the user error. May be null. Using this makes debugging
	 *            a lot easier.
	 * @param code
	 *            The error code referring to a message in the file
	 *            <code>UserErrorMessages.properties</code>
	 * @param arguments
	 *            Arguments for the short or long message.
	 */
	public UserError(Operator operator, Throwable cause, int code, Object... arguments) {
		super(getErrorMessage(code, arguments), cause);
		this.code = code;
		this.operator = operator;
		this.arguments = arguments;
	}

	/** Convenience constructor for messages with no arguments and cause. */
	public UserError(Operator operator, Throwable cause, int code) {
		this(operator, code, new Object[0], cause);
	}

	public UserError(Operator operator, int code, Object... arguments) {
		this(operator, null, code, arguments);
	}

	/** Convenience constructor for messages with no arguments. */
	public UserError(Operator operator, int code) {
		this(operator, null, code, new Object[0]);
	}

	public UserError(Operator operator, Throwable cause, String errorId, Object... arguments) {
		super(getErrorMessage(errorId, arguments), cause);
		this.code = -1;
		this.errorIdentifier = errorId;
		this.operator = operator;
		this.arguments = arguments;
	}

	/**
	 * Convenience constructor for messages with no arguments and cause. This constructor is in fact
	 * equivalent to the call of the above constructor but must kept for compatibility issues for
	 * existing compiled extensions.
	 */
	public UserError(Operator operator, Throwable cause, String errorId) {
		this(operator, cause, errorId, new Object[0]);
	}

	public UserError(Operator operator, String errorId, Object... arguments) {
		this(operator, null, errorId, arguments);
	}

	/** Convenience constructor for messages with no arguments. */
	public UserError(Operator operator, String errorId) {
		this(operator, null, errorId, new Object[0]);
	}

	@Override
	public String getDetails() {
		if (errorIdentifier == null) {
			return getResourceString(code, "long", "Description missing.");
		} else {
			// allow arguments for error details of new user errors
			String message = getResourceString(errorIdentifier, "long", "Description missing.");
			return addArguments(arguments, message);
		}
	}

	@Override
	public String getErrorName() {
		if (errorIdentifier == null) {
			return getResourceString(code, "name", "Unnamed error.");
		} else {
			return getResourceString(errorIdentifier, "name", "Unnamed error.");
		}
	}

	@Override
	public int getCode() {
		return code;
	}

	/**
	 * Returns the ErrorIdentifier if the UserError was created with a constructor that specifies
	 * an error ID. Returns null if the UserError was created with a constructor that specifies an
	 * error code.
	 */
	public String getErrorIdentifier() {
		return errorIdentifier;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public static String getErrorMessage(int code, Object[] arguments) {
		String message = getResourceString(code, "short", "No message.");
		return addArguments(arguments, message);

	}

	public static String getErrorMessage(String identifier, Object[] arguments) {
		String message = getResourceString(identifier, "short", "No message.");
		return addArguments(arguments, message);
	}

	/**
	 * Adds the arguments to the message.
	 *
	 * @param arguments
	 * @param message
	 * @return the message including the arguments or the message of the exception if one occurs
	 */
	private static String addArguments(Object[] arguments, String message) {
		try {
			formatter.applyPattern(message);
			String formatted = formatter.format(arguments);
			return formatted;
		} catch (Throwable t) {
			return message;
		}
	}

	/**
	 * Returns a resource message for the given error code.
	 *
	 * @param key
	 *            one out of &quot;name&quot;, &quot;short&quot;, &quot;long&quot;
	 */
	public static String getResourceString(int code, String key, String deflt) {
		return getResourceString(code + "", key, deflt);
	}

	/**
	 * This returns a resource message of the internationalized error messages identified by an id.
	 * Compared to the legacy method {@link #getResourceString(int, String, String)} this supports a
	 * more detailed identifier. This makes it easier to ensure extensions don't reuse already
	 * defined core errors. It is common sense to add the extensions namespace identifier as second
	 * part of the key, just after error. For example: error.rmx_web.operator.unusable = This
	 * operator {0} is unusable.
	 *
	 * @param id
	 *            The identifier of the error. "error." will be automatically prepended-
	 * @param key
	 *            The part of the error description that should be shown.
	 * @param deflt
	 *            The default if no resource bundle is available.
	 */
	public static String getResourceString(String id, String key, String deflt) {
		return deflt;
	}
}
