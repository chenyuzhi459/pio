package io.sugo.pio.engine.detail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.detail.data.DetailModelData;
import io.sugo.pio.engine.detail.data.DetailPreparedData;
import io.sugo.pio.engine.detail.data.DetailTrainingData;
import io.sugo.pio.engine.training.*;

/**
 */
public class DetailEngineFactory implements EngineFactory {
    private final BatchEventHose batchEventHose;
    private final Repository repository;
    private final DetailEngineParams engineParams;

    @JsonCreator
    public DetailEngineFactory(@JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                               @JsonProperty("repository") Repository repository,
                               @JsonProperty("engineParams") DetailEngineParams engineParams) {
        this.batchEventHose = batchEventHose;
        this.repository = repository;
        this.engineParams = engineParams;
    }

    @JsonProperty
    public BatchEventHose getBatchEventHose() {
        return batchEventHose;
    }

    @JsonProperty
    public Repository getRepository() {
        return repository;
    }

    @JsonProperty
    public DetailEngineParams getEngineParams() {
        return engineParams;
    }

    @Override
    public Engine createEngine() {
        return new DetailEngine(batchEventHose, repository, engineParams);
    }
}

