package io.sugo.pio.ports.metadata;

import io.sugo.pio.operator.IOObject;
import io.sugo.pio.ports.InputPort;

/**
 * Indicates that input for a port was missing or had the wrong type.
 * 
 * @author Simon Fischer
 */
public class InputMissingMetaDataError extends SimpleMetaDataError {

	private InputPort inputPort;
	private Class<? extends IOObject> desiredClass;

	public InputMissingMetaDataError(InputPort inputPort, Class<? extends IOObject> desiredClazz) {
		this(inputPort, desiredClazz, null);
	}

	public InputMissingMetaDataError(InputPort inputPort, Class<? extends IOObject> desiredClass,
                                     Class<? extends IOObject> receivedClass) {
		super(Severity.ERROR, inputPort, (receivedClass == null ? "input_missing" : "expected"),
				(receivedClass == null ? new Object[] { inputPort.getSpec() } : new Object[] { desiredClass.getSimpleName(),
						receivedClass.getSimpleName() }));
		this.inputPort = inputPort;
		this.desiredClass = desiredClass;
	}
}
