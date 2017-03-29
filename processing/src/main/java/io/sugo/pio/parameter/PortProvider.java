package io.sugo.pio.parameter;

import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.Port;


/**
 * This interfaces provides the possibility to retrieve Ports during runtime to check for example if
 * the {@link InputPort} is connected or not.
 * 
 * 
 * @author Nils Woehler
 * 
 */
public interface PortProvider {

	/** Returns the desired {@link Port}. */
	public Port getPort();

}
