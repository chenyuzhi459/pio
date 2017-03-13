package io.sugo.pio.operator.learner.functions.kernel;

import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.kernel.Kernel;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.svm.SVMInterface;
import io.sugo.pio.operator.learner.functions.kernel.logistic.KLR;


/**
 * The model for the MyKLR learner by Stefan Rueping.
 * 
 */
public class MyKLRModel extends AbstractMySVMModel {

	private static final long serialVersionUID = 8033254475867697195L;

	public MyKLRModel(ExampleSet exampleSet,
					  io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples model, Kernel kernel, int kernelType) {
		super(exampleSet, model, kernel, kernelType);
	}

	public String getModelInfo() {
		return "KLR Model (" + getNumberOfSupportVectors() + " support vectors)";
	}

	@Override
	public SVMInterface createSVM() {
		return new KLR();
	}

	@Override
	public void setPrediction(Example example, double _prediction) {
		double prediction = _prediction - 0.5d;
		Attribute predLabel = example.getAttributes().getPredictedLabel();
		int index = prediction > 0.0 ? predLabel.getMapping().getPositiveIndex() : predLabel.getMapping().getNegativeIndex();
		example.setValue(predLabel, index);
		example.setConfidence(predLabel.getMapping().getPositiveString(), 1.0d / (1.0d + Math.exp(-prediction)));
		example.setConfidence(predLabel.getMapping().getNegativeString(), 1.0d / (1.0d + Math.exp(prediction)));
	}
}
