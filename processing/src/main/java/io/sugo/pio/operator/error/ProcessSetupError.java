package io.sugo.pio.operator.error;


import io.sugo.pio.ports.PortOwner;



/**
 */
public interface ProcessSetupError {

	/** Severity levels of ProcessSetupErrors. */
	public enum Severity {
		/**
		 * This indicates that the corresponding message is just for information
		 */
		INFORMATION,
		/**
		 * This is an indicator of wrong experiment setup, but the process may run nevertheless.
		 */
		WARNING,
		/** Process will definitely (well, say, most certainly) not run. */
		ERROR
	}

	/** Returns the human readable, formatted message. */
	public String getMessage();

	/**
	 * Returns the owner of the port that should be displayed by the GUI to fix the error.
	 */
	public PortOwner getOwner();

	/** Returns the severity of the error. */
	public Severity getSeverity();
}
