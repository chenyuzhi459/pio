package io.sugo.pio.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes(value = {
        @JsonSubTypes.Type(name = "prediction", value = PredictionQuery.class)
})
public interface Query<Q> {
    @JsonProperty
    public String getType();

    @JsonProperty
    public Q getQueryObject();
}
