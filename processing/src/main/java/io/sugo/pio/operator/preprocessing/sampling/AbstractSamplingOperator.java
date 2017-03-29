package io.sugo.pio.operator.preprocessing.sampling;

import io.sugo.pio.operator.preprocessing.AbstractDataProcessing;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MDInteger;
import io.sugo.pio.ports.metadata.MetaData;


/**
 * Abstract superclass of operators leaving the attribute set and data unchanged but reducing the
 * number of examples.
 *
 */
public abstract class AbstractSamplingOperator extends AbstractDataProcessing {

    public AbstractSamplingOperator() {
        super();
    }

    @Override
    protected MetaData modifyMetaData(ExampleSetMetaData metaData) {
        try {
            metaData.setNumberOfExamples(getSampledSize(metaData));
        } catch (UndefinedParameterError e) {
            metaData.setNumberOfExamples(new MDInteger());
        }
        return metaData;
    }

    /**
     * subclasses must implement this method for exact size meta data.
     */
    protected abstract MDInteger getSampledSize(ExampleSetMetaData emd) throws UndefinedParameterError;

    @Override
    public boolean writesIntoExistingData() {
        return false;
    }
}
