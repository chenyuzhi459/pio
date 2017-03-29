package io.sugo.pio.ports.metadata;

import io.sugo.pio.ports.Port;
import io.sugo.pio.ports.InputPort;

/**
 * An Error that can be registered to an {@link InputPort} to show up in the GUI. This errors are
 * created during the MetaDataTransformation of a process and should give the use the ability to
 * find errors and problems before executing the process.
 * 
 * @author Simon Fischer
 */
public class SimpleMetaDataError extends SimpleProcessSetupError implements MetaDataError {

	private Port port;

	/**
	 * Constructor for an error. Please note, that the i18nKey will be appended to "metadata.error."
	 * to form the final key.
	 */
	public SimpleMetaDataError(Severity severity, Port port, String i18nKey, Object... i18nArgs) {
		super(severity, port == null ? null : port.getPorts().getOwner(), i18nKey, i18nArgs);
		this.port = port;
	}

	@Override
	public Port getPort() {
		return port;
	}
}
