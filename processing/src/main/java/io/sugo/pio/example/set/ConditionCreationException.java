package io.sugo.pio.example.set;

/**
 * Exception class whose instances are thrown during the creation of conditions.
 * 
 * @author Ingo Mierswa ingomierswa Exp $
 */
public class ConditionCreationException extends Exception {

	private static final long serialVersionUID = -7648754234739697969L;

	public ConditionCreationException(String message) {
		super(message);
	}

	public ConditionCreationException(String message, Throwable cause) {
		super(message, cause);
	}
}
