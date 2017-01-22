package io.sugo.pio.tools.cipher;

/**
 * This exception will be thrown if a problem during key generation occurs.
 * 
 */
public class KeyGenerationException extends Exception {

	private static final long serialVersionUID = -251163459747969941L;

	public KeyGenerationException(String message) {
		super(message);
	}
}
