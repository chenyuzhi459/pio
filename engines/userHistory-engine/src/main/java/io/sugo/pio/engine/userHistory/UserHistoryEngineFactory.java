package io.sugo.pio.engine.userHistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.training.*;
import io.sugo.pio.engine.userHistory.data.UserHistoryModelData;
import io.sugo.pio.engine.userHistory.data.UserHistoryPreparaData;
import io.sugo.pio.engine.userHistory.data.UserHistoryTrainingData;
import io.sugo.pio.engine.userHistory.engine.UserHistoryAlgorithm;
import io.sugo.pio.engine.userHistory.engine.UserHistoryDatasource;
import io.sugo.pio.engine.userHistory.engine.UserHistoryModel;
import io.sugo.pio.engine.userHistory.engine.UserHistoryPreparator;

/**
 */
public class UserHistoryEngineFactory implements EngineFactory<UserHistoryTrainingData, UserHistoryPreparaData, UserHistoryModelData> {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;


    @JsonCreator
    public UserHistoryEngineFactory(@JsonProperty("propertyHose") PropertyHose propertyHose,
                               @JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                               @JsonProperty("repository") Repository repository) {
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
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

    @Override
    public Engine createEngine() {
        return new UserHistoryEngine(propertyHose, batchEventHose, repository);
    }
}
