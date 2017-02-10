package io.sugo.pio.engine.userHistory;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.training.TrainingConfig;

/**
 */
public class UserHistoryTrainingConfig extends TrainingConfig<UserHistoryEngineFactory> {
    @JsonCreator
    public UserHistoryTrainingConfig(@JsonProperty("engineFactory") UserHistoryEngineFactory engineFactory) {
        super(engineFactory);
    }
}
