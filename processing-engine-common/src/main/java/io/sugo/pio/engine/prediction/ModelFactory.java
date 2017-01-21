package io.sugo.pio.engine.prediction;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.sugo.pio.engine.data.output.Repository;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface ModelFactory<R> {
    PredictionModel<R> loadModel(Repository repository);
}
