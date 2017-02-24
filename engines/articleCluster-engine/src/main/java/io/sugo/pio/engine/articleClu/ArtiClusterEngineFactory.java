package io.sugo.pio.engine.articleClu;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.training.*;

/**
 */
public class ArtiClusterEngineFactory implements EngineFactory {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final ArtiClusterEngineParams engineParams;

    @JsonCreator
    public ArtiClusterEngineFactory(@JsonProperty("propertyHose") PropertyHose propertyHose,
                                    @JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                                    @JsonProperty("repository") Repository repository,
                                    @JsonProperty("engineParams") ArtiClusterEngineParams engineParams) {
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
        this.engineParams = engineParams;
    }
    @Override
    public Engine createEngine() {
        return new ArtiClusterEngine(propertyHose, batchEventHose, repository, engineParams);
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
    public ArtiClusterEngineParams getEngineParams() {
        return engineParams;
    }
}
