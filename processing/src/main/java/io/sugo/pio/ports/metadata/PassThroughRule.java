package io.sugo.pio.ports.metadata;

import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

/**
 * Assigns meta data received from an input port to an output port. Useful e.g. for preprocessing
 * operators. If meta data changes dynamically, can be modified by overriding
 * {@link #modifyMetaData(MetaData)}.
 * 
 * @author Simon Fischer
 * */
public class PassThroughRule implements MDTransformationRule {

	private final OutputPort outputPort;

	private final InputPort inputPort;
	private final boolean optional;

	public PassThroughRule(InputPort inputPort, OutputPort outputPort, boolean mandatory) {
		super();
		this.outputPort = outputPort;
		this.inputPort = inputPort;
		this.optional = !mandatory;
	}

	@Override
	public void transformMD() {
		MetaData modified = inputPort.getMetaData();
		if (modified == null) {
			if (!optional) {
				inputPort.addError(new InputMissingMetaDataError(inputPort, null, null));
			}
			outputPort.deliverMD(null);
		} else {
			modified = modified.clone();
			modified.addToHistory(outputPort);
			outputPort.deliverMD(modifyMetaData(modified));
		}
	}

	/**
	 * Modifies the received meta data before it is passed to the output. Can be used if the
	 * transformation depends on parameters etc. The default implementation just returns the
	 * original. Subclasses may safely modify the meta data, since a copy is used for this method.
	 */
	public MetaData modifyMetaData(MetaData unmodifiedMetaData) {
		return unmodifiedMetaData;
	}

	public OutputPort getOutputPort() {
		return this.outputPort;
	}

	public InputPort getInputPort() {
		return this.inputPort;
	}

	public boolean isOptional() {
		return this.optional;
	}

}
