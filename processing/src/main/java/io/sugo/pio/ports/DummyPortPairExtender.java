package io.sugo.pio.ports;


import io.sugo.pio.operator.error.ProcessSetupError;
import io.sugo.pio.ports.metadata.MDTransformationRule;
import io.sugo.pio.ports.metadata.MetaData;
import io.sugo.pio.ports.metadata.SimpleProcessSetupError;

/**
 * This extender is just for operators which don't have any real input. It just should ensure the
 * correct execution order. And throw a warning if nothing is connected.
 * 
 *
 */
public class DummyPortPairExtender extends PortPairExtender {

	public DummyPortPairExtender(String name, InputPorts inPorts, OutputPorts outPorts) {
		super(name, inPorts, outPorts);
	}

	/**
	 * The generated rule copies all meta data from the generated input ports to all generated
	 * output ports. Unlike the PortPairExtender, it warns if nothing is connected.
	 */
	@Override
	public MDTransformationRule makePassThroughRule() {
		return new MDTransformationRule() {

			@Override
			public void transformMD() {
				boolean somethingConnected = false;
				for (PortPair pair : getManagedPairs()) {
					// testing if connected for execution order
					somethingConnected |= pair.getInputPort().isConnected() || pair.getOutputPort().isConnected();
					// transforming meta data.
					MetaData inData = pair.getInputPort().getMetaData();
					if (inData != null) {
						inData = transformMetaData(inData.clone());
						inData.addToHistory(pair.getOutputPort());
						pair.getOutputPort().deliverMD(inData);
					} else {
						pair.getOutputPort().deliverMD(null);
					}
				}
				if (!somethingConnected) {
					PortOwner owner = getManagedPairs().get(0).getInputPort().getPorts().getOwner();
					owner.getOperator().addError(
							new SimpleProcessSetupError(ProcessSetupError.Severity.WARNING, owner, "pio.error.process.execution_order_undefined"));
				}
			}
		};
	}
}
