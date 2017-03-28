package io.sugo.pio.ports.metadata;


import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

import java.util.Collection;

/**
 * Delivers meta data received in the first input that is not null to the output port. TODO: Make
 * intersection of meta data. This is all we can guarantee.
 * 
 * @author Simon Fischer
 */
public class ManyToOnePassThroughRule implements MDTransformationRule {

	private OutputPort outputPort;
	private Collection<InputPort> inputPorts;

	public ManyToOnePassThroughRule(Collection<InputPort> inputPorts, OutputPort outputPort) {
		this.inputPorts = inputPorts;
		this.outputPort = outputPort;
	}

	@Override
	public void transformMD() {
		for (InputPort inputPort : inputPorts) {
			MetaData metaData = inputPort.getMetaData();
			if (metaData != null) {
				metaData = metaData.clone();
				metaData.addToHistory(outputPort);
				outputPort.deliverMD(modifyMetaData(metaData));
				return;
			}
		}
		outputPort.deliverMD(null);
	}

	/**
	 * Modifies the standard meta data before it is passed to the output. Can be used if the
	 * transformation depends on parameters etc. The default implementation just returns the
	 * original. Subclasses may safely modify the meta data, since a copy is used for this method.
	 */
	public MetaData modifyMetaData(MetaData unmodifiedMetaData) {
		return unmodifiedMetaData;
	}
}
