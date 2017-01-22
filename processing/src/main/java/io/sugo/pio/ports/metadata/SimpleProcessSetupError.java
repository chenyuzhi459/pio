package io.sugo.pio.ports.metadata;

import io.sugo.pio.operator.ProcessSetupError;
import io.sugo.pio.ports.PortOwner;


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
