/**
 * Copyright (C) 2001-2016 by RapidMiner and the contributors
 *
 * Complete list of developers available at our web site:
 *
 * http://rapidminer.com
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */
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
