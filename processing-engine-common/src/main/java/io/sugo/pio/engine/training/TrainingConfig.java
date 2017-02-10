package io.sugo.pio.engine.training;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Collections;
import java.util.Map;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public abstract class TrainingConfig<EngineFactoryType extends EngineFactory> {
    private final EngineFactoryType engineFactory;
    private final Map<String, String> sparkConfSettings;
    private final SparkContextSettings sparkContextSettings;


    public TrainingConfig(
            EngineFactoryType engineFactory,
            Map<String, String> sparkConfSettings,
            SparkContextSettings sparkContextSettings) {
        this.engineFactory = engineFactory;
        this.sparkConfSettings = sparkConfSettings;
        this.sparkContextSettings = sparkContextSettings;
    }

    public TrainingConfig(
            EngineFactoryType engineFactory
    )
    {
        this(engineFactory, Collections.emptyMap(), new SparkContextSettings(null));
    }

    @JsonProperty
    public EngineFactoryType getEngineFactory() {
        return engineFactory;
    }

    @JsonProperty
    public Map<String, String> getSparkConfSettings() {
        return sparkConfSettings;
    }

    @JsonProperty
    public SparkContextSettings getSparkContextSettings() {
        return sparkContextSettings;
    }
}
