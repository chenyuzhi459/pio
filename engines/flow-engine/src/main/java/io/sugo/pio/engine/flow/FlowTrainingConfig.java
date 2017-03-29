package io.sugo.pio.engine.flow;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.training.TrainingConfig;

/**
 */
public class FlowTrainingConfig extends TrainingConfig<FlowEngineFactory> {
    @JsonCreator
    public FlowTrainingConfig(@JsonProperty("engineFactory") FlowEngineFactory engineFactory) {
        super(engineFactory);
    }
}
