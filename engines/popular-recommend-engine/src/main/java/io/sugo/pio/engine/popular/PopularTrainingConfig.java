package io.sugo.pio.engine.popular;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.training.TrainingConfig;

/**
 */
public class PopularTrainingConfig extends TrainingConfig<PopularEngineFactory> {
    @JsonCreator
    public PopularTrainingConfig(@JsonProperty("engineFactory") PopularEngineFactory engineFactory) {
        super(engineFactory);
    }
}
