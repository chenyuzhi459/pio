package io.sugo.pio.engine.userExtension;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.training.TrainingConfig;

/**
 */
public class UserExtensionTrainingConfig extends TrainingConfig<UserExtensionEngineFactory> {
    @JsonCreator
    public UserExtensionTrainingConfig(@JsonProperty("engineFactory") UserExtensionEngineFactory engineFactory) {
        super(engineFactory);
    }
}
