package io.sugo.pio.operator.io;

import java.io.IOException;


/**
 * 
 * @author Simon Fischer
 * 
 */
public class CSVParseException extends IOException {

	private static final long serialVersionUID = 1L;

	public CSVParseException(String message) {
		super(message);
	}

}
