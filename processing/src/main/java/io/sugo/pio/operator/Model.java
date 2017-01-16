package io.sugo.pio.operator;

import io.sugo.pio.example.ExampleSet;
import io.sugo.pio.example.set.HeaderExampleSet;

/**
 */
public interface Model extends ResultObject {

    /**
     * This method has to return the HeaderExampleSet containing the signature of the example set
     * during training time containing all informations about attributes. This is important for
     * checking the compatibility of the examples on application time. Note that the AbstractModel
     * already implements all necessary functionality.
     */
    public HeaderExampleSet getTrainingHeader();

    /**
     * Applies the model on the given {@link ExampleSet}. Please note that the delivered {@link ExampleSet} might
     * be the same as the input {@link ExampleSet}. This is, however, not always the case.
     */
    public ExampleSet apply(ExampleSet testSet) throws OperatorException;

    /**
     * This method can be used to allow additional parameters. Most models do not support parameters
     * during application.
     */
    public void setParameter(String key, Object value) throws OperatorException;
}
