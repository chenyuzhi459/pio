package io.sugo.pio.engine.als;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.als.data.ALSModelData;
import io.sugo.pio.engine.als.data.ALSPreparedData;
import io.sugo.pio.engine.als.data.ALSTrainingData;
import io.sugo.pio.engine.als.engine.ALSAlgorithm;
import io.sugo.pio.engine.als.engine.ALSDModel;
import io.sugo.pio.engine.als.engine.ALSDataSource;
import io.sugo.pio.engine.als.engine.ALSPreparator;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.training.*;

/**
 */
public class ALSEngineFactory implements EngineFactory<ALSTrainingData, ALSPreparedData, ALSModelData> {
    private final BatchEventHose batchEventHose;
    private final Repository repository;
    private final ALSEngineParams engineParams;

    @JsonCreator
    public ALSEngineFactory(@JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                            @JsonProperty("repository") Repository repository,
                            @JsonProperty("engineParams") ALSEngineParams engineParams) {
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
    public ALSEngineParams getEngineParams() {
        return engineParams;
    }

    @Override
    public Engine createEngine() {
        return new ALSEngine(batchEventHose, repository, engineParams);
    }
}
