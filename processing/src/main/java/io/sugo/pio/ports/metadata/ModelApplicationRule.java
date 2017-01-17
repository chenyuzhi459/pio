package io.sugo.pio.ports.metadata;

import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

/**
 * This rule might be used, if a model is applied on a data set.
 * 
 * @author Sebastian Land
 */
public class ModelApplicationRule extends PassThroughRule {

	private InputPort modelInput;

	public ModelApplicationRule(InputPort inputPort, OutputPort outputPort, InputPort modelInput, boolean mandatory) {
		super(inputPort, outputPort, mandatory);
		this.modelInput = modelInput;
	}

	@Override
	public MetaData modifyMetaData(MetaData metaData) {
		if (metaData instanceof ExampleSetMetaData) {
			MetaData modelMetaData = modelInput.getMetaData();
			if (modelMetaData instanceof ModelMetaData) {
				metaData = ((ModelMetaData) modelMetaData).apply((ExampleSetMetaData) metaData, getInputPort());
			}
		}
		return metaData;
	}

}
