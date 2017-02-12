package io.sugo.pio.engine.popular;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.popular.data.PopularModelData;
import io.sugo.pio.engine.popular.data.PopularPreparaData;
import io.sugo.pio.engine.popular.data.PopularTrainingData;
import io.sugo.pio.engine.training.*;

/**
 */
public class PopularEngineFactory implements EngineFactory {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final PopEngineParams engineParams;

    @JsonCreator
    public PopularEngineFactory(@JsonProperty("propertyHose") PropertyHose propertyHose,
                           @JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                           @JsonProperty("repository") Repository repository,
                           @JsonProperty("engineParams") PopEngineParams engineParams) {
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
        this.engineParams = engineParams;
    }


    @Override
    public Engine createEngine() {
        return new PopularEngine(propertyHose, batchEventHose, repository, engineParams);
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
    public PopEngineParams getEngineParams() {
        return engineParams;
    }

    @JsonProperty
    public Repository getRepository() {
        return repository;
    }
}
