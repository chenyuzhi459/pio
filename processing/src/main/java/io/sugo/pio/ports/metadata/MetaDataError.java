package io.sugo.pio.ports.metadata;


import io.sugo.pio.operator.error.ProcessSetupError;
import io.sugo.pio.ports.Port;

/**
 * An error that belongs to a port.
 * 
 * @author Simon Fischer
 */
public interface MetaDataError extends ProcessSetupError {

	/** Returns the port where the error occurred. */
	public Port getPort();

}
