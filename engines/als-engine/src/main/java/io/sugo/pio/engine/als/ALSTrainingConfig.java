package io.sugo.pio.engine.als;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.training.SparkContextSettings;
import io.sugo.pio.engine.training.TrainingConfig;

import java.util.Map;

/**
 */
public class ALSTrainingConfig extends TrainingConfig<ALSEngineFactory> {
    @JsonCreator
    public ALSTrainingConfig(@JsonProperty("engineFactory") ALSEngineFactory engineFactory,
                             @JsonProperty("sparkConfSettings") Map<String, String> sparkConfSettings,
                             @JsonProperty("sparkContextSettings") SparkContextSettings sparkContextSettings) {
        super(engineFactory, sparkConfSettings, sparkContextSettings);
    }
}
