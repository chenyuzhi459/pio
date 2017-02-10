package io.sugo.pio.engine.search;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.training.TrainingConfig;

/**
 */
public class SearchTrainingConfig extends TrainingConfig<SearchEngineFactory> {
    @JsonCreator
    public SearchTrainingConfig(@JsonProperty("engineFactory") SearchEngineFactory engineFactory) {
        super(engineFactory);
    }
}

