package io.sugo.pio.example;

/**
 * This exception will be thrown if operators use properties of attributes which are not supported
 * by this attribute, for example, if a nominal mapping of the third value is retrieved from a
 * binominal attribute.
 * 
 */
public class AttributeTypeException extends RuntimeException {

	private static final long serialVersionUID = -990113662782113571L;

	public AttributeTypeException(String message) {
		super(message);
	}
}
