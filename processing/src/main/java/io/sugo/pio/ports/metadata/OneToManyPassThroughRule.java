package io.sugo.pio.ports.metadata;

import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;
import io.sugo.pio.ports.OutputPorts;

import java.util.Collection;


/**
 * A rule which copies meta data from one input port to several output ports.
 * 
 * @author Simon Fischer
 * 
 */
public class OneToManyPassThroughRule implements MDTransformationRule {

	private final InputPort inputPort;
	private final Collection<OutputPort> outputPorts;

	public OneToManyPassThroughRule(InputPort inputPort, OutputPorts outputPorts) {
		this(inputPort, outputPorts.getAllPorts());
	}

	public OneToManyPassThroughRule(InputPort inputPort, Collection<OutputPort> outputPorts) {
		this.inputPort = inputPort;
		this.outputPorts = outputPorts;
	}

	@Override
	public void transformMD() {
		int i = 0;
		for (OutputPort outputPort : outputPorts) {
			MetaData metaData = inputPort.getMetaData();
			if (metaData != null) {
				metaData = metaData.clone();
				metaData.addToHistory(outputPort);
				outputPort.deliverMD(modifyMetaData(metaData, i));
			} else {
				outputPort.deliverMD(null);
			}
			i++;
		}
	}

	/**
	 * Modifies the received meta data before it is passed to the output. Can be used if the
	 * transformation depends on parameters etc. The default implementation just returns the
	 * original. Subclasses may safely modify the meta data, since a copy is used for this method.
	 * 
	 * @param outputIndex
	 *            TODO
	 */
	public MetaData modifyMetaData(MetaData unmodifiedMetaData, int outputIndex) {
		return unmodifiedMetaData;
	}
}
