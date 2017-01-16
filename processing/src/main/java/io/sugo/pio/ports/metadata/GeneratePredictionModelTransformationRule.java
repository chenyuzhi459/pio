package io.sugo.pio.ports.metadata;

import io.sugo.pio.operator.learner.PredictionModel;
import io.sugo.pio.ports.InputPort;
import io.sugo.pio.ports.OutputPort;

/**
 * @author Simon Fischer
 */
public class GeneratePredictionModelTransformationRule implements MDTransformationRule {

	private final OutputPort outputPort;
	private final InputPort exampleSetInput;
	private final Class<? extends PredictionModel> modelClass;

	public GeneratePredictionModelTransformationRule(InputPort exampleSetInput, OutputPort outputPort,
                                                     Class<? extends PredictionModel> modelClass) {
		this.outputPort = outputPort;
		this.exampleSetInput = exampleSetInput;
		this.modelClass = modelClass;
	}

	@Override
	public void transformMD() {
		MetaData input = exampleSetInput.getMetaData();
		PredictionModelMetaData mmd;
		if (input != null && input instanceof ExampleSetMetaData) {
			mmd = new PredictionModelMetaData(modelClass, (ExampleSetMetaData) input);
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
