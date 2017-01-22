package io.sugo.pio.tools.cipher;

/**
 * This exception will be thrown if a problem during encryption or decryption occurs.
 * 
 */
public class CipherException extends Exception {

	private static final long serialVersionUID = -5070414835484266101L;

	public CipherException(String message) {
		super(message);
	}
}
