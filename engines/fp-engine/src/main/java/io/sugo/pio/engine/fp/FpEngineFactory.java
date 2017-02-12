package io.sugo.pio.engine.fp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.fp.data.FpModelData;
import io.sugo.pio.engine.fp.data.FpPreparaData;
import io.sugo.pio.engine.fp.data.FpTrainingData;
import io.sugo.pio.engine.training.*;

/**
 */
public class FpEngineFactory implements EngineFactory {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final FpEngineParams engineParams;

    @JsonCreator
    public FpEngineFactory(@JsonProperty("propertyHose") PropertyHose propertyHose,
                           @JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                           @JsonProperty("repository") Repository repository,
                           @JsonProperty("engineParams") FpEngineParams engineParams) {
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
        this.engineParams = engineParams;
    }

    @Override
    public Engine createEngine() {
        return new FpEngine(propertyHose, batchEventHose, repository, engineParams);
    }

    @JsonProperty
    public BatchEventHose getBatchEventHose() {
        return batchEventHose;
    }

    @JsonProperty
    public PropertyHose getPropertyHose() {
        return propertyHose;
    }

    @JsonProperty
    public Repository getRepository() {
        return repository;
    }

    @JsonProperty
    public FpEngineParams getEngineParams() {
        return engineParams;
    }

}