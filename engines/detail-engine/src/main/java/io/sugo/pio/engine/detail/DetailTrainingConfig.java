package io.sugo.pio.engine.detail;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.training.TrainingConfig;

/**
 */
public class DetailTrainingConfig extends TrainingConfig<DetailEngineFactory> {
    @JsonCreator
    public DetailTrainingConfig(@JsonProperty("engineFactory") DetailEngineFactory engineFactory) {
        super(engineFactory);
    }
}
