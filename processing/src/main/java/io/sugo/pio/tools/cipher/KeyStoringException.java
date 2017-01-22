package io.sugo.pio.tools.cipher;

/**
 * This exception will be thrown if a problem during key storing occurs.
 *
 */
public class KeyStoringException extends Exception {

	private static final long serialVersionUID = -251163459747969941L;

	public KeyStoringException(String message, Exception e) {
		super(message, e);
	}
}
