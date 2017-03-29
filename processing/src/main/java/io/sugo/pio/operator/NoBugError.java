package io.sugo.pio.operator;

/**
 * All exceptions that are no bugs (but caused by an error of the user) should implement this
 * interface in order to indicate that this should <i>not</i> issue a bug report.
 * 
 * @author Simon Fischer, Ingo Mierswa
 */
public interface NoBugError {

	/** Returns the error details/description. */
	public String getDetails();

	/** Returns the error name. */
//	public String getErrorName();

	/** Returns the error code. */
//	public int getCode();

}
