package io.sugo.pio.operator.learner.functions.kernel;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.example.Attribute;
import io.sugo.pio.example.Example;
import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.kernel.Kernel;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.svm.SVMInterface;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.svm.SVMpattern;
import io.sugo.pio.operator.learner.functions.kernel.jmysvm.svm.SVMregression;


/**
 * The implementation for the mySVM model (Java version) by Stefan Rueping.
 */
public class JMySVMModel extends AbstractMySVMModel {

    private static final long serialVersionUID = 7748169156351553025L;

    public JMySVMModel(ExampleSet exampleSet,
                       io.sugo.pio.operator.learner.functions.kernel.jmysvm.examples.SVMExamples model, Kernel kernel, int kernelType) {
        super(exampleSet, model, kernel, kernelType);
    }

    @Override
    public SVMInterface createSVM() {
        if (getLabel().isNominal()) {
            return new SVMpattern();
        } else {
            return new SVMregression();
        }
    }

    @Override
    public void setPrediction(Example example, double prediction) {
        Attribute predLabel = example.getAttributes().getPredictedLabel();
        if (predLabel.isNominal()) {
            int index = prediction > 0 ? predLabel.getMapping().getPositiveIndex() : predLabel.getMapping()
                    .getNegativeIndex();
            example.setValue(predLabel, index);
            // set confidence to numerical prediction, such that can be scaled later
            example.setConfidence(predLabel.getMapping().getPositiveString(),
                    1.0d / (1.0d + Math.exp(-prediction)));
            example.setConfidence(predLabel.getMapping().getNegativeString(), 1.0d / (1.0d + Math.exp(prediction)));
        } else {
            example.setValue(predLabel, prediction);
        }
    }

    @JsonProperty
    public String getKernelModel() {
        return super.toString();
    }

}
