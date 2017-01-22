package io.sugo.pio.query;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.prediction.PredictionQueryObject;

/**
 */
public class PredictionQuery implements Query {
    private final PredictionQueryObject predictionQueryObject;

    @JsonCreator
    public PredictionQuery(@JsonProperty("queryObject") PredictionQueryObject predictionQueryObject) {
        this.predictionQueryObject = predictionQueryObject;
    }

    @Override
    public String getType() {
        return predictionQueryObject.getType();
    }

    @Override
    public PredictionQueryObject getQueryObject() {
        return predictionQueryObject;
    }
}
