package io.sugo.pio.engine.userExtension;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.prediction.PredictionQueryObject;

public class UserExtensionQuery implements PredictionQueryObject {
    @Override
    public String getType() {
        return "userExtension";
    }
}