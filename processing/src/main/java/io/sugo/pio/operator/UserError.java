/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 * <p>
 * Complete list of developers available at our web site:
 * <p>
 * http://rapidminer.com
 * <p>
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
package io.sugo.pio.operator;

import io.sugo.pio.i18n.I18N;

import java.text.MessageFormat;


public class UserError extends OperatorException implements NoBugError {

    private static final long serialVersionUID = -8441036860570180869L;

    private static final MessageFormat formatter = new MessageFormat("");

    private String errorIdentifier = null;

    private transient Operator operator;

    private final Object[] arguments;

    public UserError(Operator operator, Throwable cause, String errorId, Object... arguments) {
        super(getErrorMessage(errorId, arguments), cause);
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

    /**
     * Convenience constructor for messages with no arguments.
     */
    public UserError(Operator operator, String errorId) {
        this(operator, null, errorId, new Object[0]);
    }

    @Override
    public String getDetails() {
        // allow arguments for error details of new user errors
        String message = getErrorMessage(errorIdentifier, arguments);
        return addArguments(arguments, message);
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

    // Read error message from self-define 'error_zh_CN.properties' file
    public static String getErrorMessage(String errorId, Object[] arguments) {
        String message = I18N.getErrorMessage(errorId);
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

}
