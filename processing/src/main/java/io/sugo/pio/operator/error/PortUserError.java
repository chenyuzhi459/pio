package io.sugo.pio.operator.error;

import io.sugo.pio.operator.UserError;
import io.sugo.pio.ports.Port;


/**
 * Subclass of a {@link UserError} which is thrown when the cause of the error is a {@link Port}.
 *
 * @since 6.5.0
 */
public class PortUserError extends UserError {

	private static final long serialVersionUID = 1090006774683438376L;

	/** the port that caused the error */
	private transient Port port;

	/** the expected data type for this port, can be null */
	private transient Class<?> expectedType;

	/** the actually delivered data type for this port, can be null */
	private transient Class<?> actualType;

	/**
	 * Creates a new user error for a port.
	 *
	 * @param port
	 *            The {@link Port} which caused the error.
	 * @param errorId
	 *            The error id referring to a message in the file
	 *            <code>UserErrorMessages.properties</code>
	 * @param arguments
	 *            Arguments for the short or long message.
	 */
	public PortUserError(Port port, String errorId, Object... arguments) {
		super(port.getPorts().getOwner().getOperator(), errorId, arguments);
		this.port = port;
	}

	/**
	 * @return the port which caused the error.
	 */
	public Port getPort() {
		return port;
	}

	/**
	 * Setter for the port which causes the error.
	 *
	 * @param port
	 *            the port
	 */
	public void setPort(Port port) {
		this.port = port;
	}

	/**
	 * Sets the expected data type.
	 *
	 * @param expectedType
	 *            the type
	 */
	public void setExpectedType(Class<?> expectedType) {
		this.expectedType = expectedType;
	}

	/**
	 * @return the expected data type for this port. Can be {@code null}
	 */
	public Class<?> getExpectedType() {
		return expectedType;
	}

	/**
	 * Sets the actual delivered data type.
	 *
	 * @param actualType
	 *            the type
	 */
	public void setActualType(Class<?> actualType) {
		this.actualType = actualType;
	}

	/**
	 * @return the actual data type delivered for this port. Can be {@code null}
	 */
	public Class<?> getActualType() {
		return actualType;
	}
}
