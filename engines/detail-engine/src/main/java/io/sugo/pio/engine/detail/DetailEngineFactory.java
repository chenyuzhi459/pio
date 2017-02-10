package io.sugo.pio.engine.detail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.detail.data.DetailModelData;
import io.sugo.pio.engine.detail.data.DetailPreparedData;
import io.sugo.pio.engine.detail.data.DetailTrainingData;
import io.sugo.pio.engine.detail.engine.DetailAlgorithm;
import io.sugo.pio.engine.detail.engine.DetailDataSource;
import io.sugo.pio.engine.detail.engine.DetailModel;
import io.sugo.pio.engine.detail.engine.DetailPreparator;
import io.sugo.pio.engine.training.*;

/**
 */
public class DetailEngineFactory implements EngineFactory<DetailTrainingData, DetailPreparedData, DetailModelData> {
    private final BatchEventHose batchEventHose;
    private final Repository repository;

    @JsonCreator
    public DetailEngineFactory(@JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                               @JsonProperty("repository") Repository repository) {
        this.batchEventHose = batchEventHose;
        this.repository = repository;
    }

    @JsonProperty
    public BatchEventHose getBatchEventHose() {
        return batchEventHose;
    }

    @JsonProperty
    public Repository getRepository() {
        return repository;
    }

    @Override
    public Engine createEngine() {
        return new DetailEngine(batchEventHose, repository);
    }
}

