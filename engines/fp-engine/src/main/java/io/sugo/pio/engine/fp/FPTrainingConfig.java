package io.sugo.pio.engine.fp;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.training.TrainingConfig;

/**
 */
public class FPTrainingConfig extends TrainingConfig<FpEngineFactory> {
    @JsonCreator
    public FPTrainingConfig(@JsonProperty FpEngineFactory engineFactory) {
        super(engineFactory);
    }
}
