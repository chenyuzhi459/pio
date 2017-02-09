package io.sugo.pio.engine.als;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.training.TrainingConfig;

/**
 */
public class ALSTrainingConfig extends TrainingConfig<ALSEngineFactory> {
    @JsonCreator
    public ALSTrainingConfig(@JsonProperty("engineFactory") ALSEngineFactory engineFactory) {
        super(engineFactory);
    }
}
