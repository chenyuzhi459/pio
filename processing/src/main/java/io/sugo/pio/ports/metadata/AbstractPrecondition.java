package io.sugo.pio.ports.metadata;

import io.sugo.pio.operator.ProcessSetupError.Severity;
import io.sugo.pio.ports.InputPort;

public abstract class AbstractPrecondition implements Precondition {

	private final InputPort inputPort;

	public AbstractPrecondition(InputPort inputPort) {
		this.inputPort = inputPort;
	}

	protected InputPort getInputPort() {
		return inputPort;
	}

	protected void createError(Severity severity, String i18nKey, Object... i18nArgs) {
		getInputPort().addError(new SimpleMetaDataError(severity, getInputPort(), i18nKey, i18nArgs));
	}

	@Override
	public String toString() {
		return getDescription();
	}
}
