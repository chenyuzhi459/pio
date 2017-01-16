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

import io.sugo.pio.operator.ProcessSetupError;
import io.sugo.pio.ports.PortOwner;


/**
 * @author Simon Fischer
 */
public class SimpleProcessSetupError implements ProcessSetupError {

	private String i18nKey;
	private final Object[] i18nArgs;
	private final PortOwner owner;
	private final Severity severity;

	public SimpleProcessSetupError(Severity severity, PortOwner owner, String i18nKey, Object... i18nArgs) {
		this(severity, owner, false, i18nKey, i18nArgs);
	}

	public SimpleProcessSetupError(Severity severity, PortOwner portOwner,
                                   boolean absoluteKey, String i18nKey, Object... i18nArgs) {
		super();
		if (absoluteKey) {
			this.i18nKey = i18nKey;
		} else {
			this.i18nKey = "process.error." + i18nKey;
		}
		this.i18nArgs = i18nArgs;
		this.owner = portOwner;
		this.severity = severity;
	}

	@Override
	public final PortOwner getOwner() {
		return owner;
	}

	@Override
	public final Severity getSeverity() {
		return severity;
	}

}
