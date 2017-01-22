package io.sugo.pio.engine.prediction;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "noop", value = NoopQueryObject.class)
})
public interface PredictionQueryObject {
    public String getType();
}
