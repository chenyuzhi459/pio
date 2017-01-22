package io.sugo.pio.tools.cipher;

/**
 * This exception will be thrown if a problem during key loading occurs.
 *
 */
public class KeyLoadingException extends Exception {

	private static final long serialVersionUID = -251163459747969941L;

	public KeyLoadingException(String message) {
		super(message);
	}
}
