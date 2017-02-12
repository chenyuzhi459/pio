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

/**
 */
public class UserHistoryEngineFactory implements EngineFactory {
    private final BatchEventHose batchEventHose;
    private final PropertyHose propertyHose;
    private final Repository repository;
    private final UserHistoryEngineParams engineParams;


    @JsonCreator
    public UserHistoryEngineFactory(@JsonProperty("propertyHose") PropertyHose propertyHose,
                                    @JsonProperty("batchEventHose") BatchEventHose batchEventHose,
                                    @JsonProperty("repository") Repository repository,
                                    @JsonProperty("engineParams") UserHistoryEngineParams engineParams) {
        this.batchEventHose = batchEventHose;
        this.propertyHose = propertyHose;
        this.repository = repository;
        this.engineParams = engineParams;
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
    public UserHistoryEngineParams getEngineParams() {
        return engineParams;
    }

    @Override
    public Engine createEngine() {
        return new UserHistoryEngine(propertyHose, batchEventHose, repository, engineParams);
    }
}
