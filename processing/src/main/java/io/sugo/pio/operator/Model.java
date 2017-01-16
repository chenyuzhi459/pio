package io.sugo.pio.operator;

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
}
