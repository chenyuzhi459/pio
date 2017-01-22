package io.sugo.pio.engine.prediction;

/**
 */
public interface PredictionModel<R> {
    public R predict(PredictionQueryObject query);
}
