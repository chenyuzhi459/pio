package io.sugo.pio.operator.processing.filter;

/**
 * Exception class whose instances are thrown during the creation of conditions.
 * 
 */
public class ConditionCreationException extends RuntimeException {

	private static final long serialVersionUID = -7648754234739697969L;

	public ConditionCreationException(String message) {
		super(message);
	}

	public ConditionCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
