package io.sugo.pio.engine.training;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public abstract class TrainingConfig<EngineFactoryType extends EngineFactory> {
    private EngineFactoryType engineFactory;

    public TrainingConfig(
            EngineFactoryType engineFactory
    )
    {
        this.engineFactory = engineFactory;
    }

    @JsonProperty
    public EngineFactoryType getEngineFactory() {
        return engineFactory;
    }
}
