package io.sugo.pio.ports.metadata;

import io.sugo.pio.operator.Model;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

/**
 * @author Simon Fischer
 */
public class GenerateModelTransformationRule implements MDTransformationRule {

	private final OutputPort outputPort;
	private final InputPort exampleSetInput;
	private final Class<? extends Model> modelClass;

	public GenerateModelTransformationRule(InputPort exampleSetInput, OutputPort outputPort,
                                           Class<? extends Model> modelClass) {
		this.outputPort = outputPort;
		this.exampleSetInput = exampleSetInput;
		this.modelClass = modelClass;
	}

	@Override
	public void transformMD() {
		MetaData input = exampleSetInput.getMetaData();
		ModelMetaData mmd;
		if (input != null && input instanceof ExampleSetMetaData) {
			mmd = new ModelMetaData(modelClass, (ExampleSetMetaData) input);
			mmd.addToHistory(outputPort);
			outputPort.deliverMD(mmd);
			return;
		}
		outputPort.deliverMD(null);
		return;
	}

	/**
	 * @return the {@link OutputPort} the MD rule is for
	 */
	public OutputPort getOutputPort() {
		return outputPort;
	}
}
