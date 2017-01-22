package io.sugo.pio.operator.features.selection;


import io.sugo.pio.operator.features.AbstractFeatureProcessing;
import io.sugo.pio.parameter.UndefinedParameterError;
import io.sugo.pio.ports.metadata.ExampleSetMetaData;
import io.sugo.pio.ports.metadata.MetaData;

/**
 * Abstract superclass of all feature processing operators who remove features from the example set.
 */
public abstract class AbstractFeatureSelection extends AbstractFeatureProcessing {

    @Override
    protected MetaData modifyMetaData(ExampleSetMetaData metaData) throws UndefinedParameterError {
        metaData.attributesAreSubset();
        return metaData;
    }

    @Override
    public boolean writesIntoExistingData() {
        return false;
    }
}
