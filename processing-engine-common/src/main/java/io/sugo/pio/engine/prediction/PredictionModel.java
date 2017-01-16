package io.sugo.pio.engine.prediction;

/**
 */
public interface PredictionModel<Q, R> {
    public R predict(Q query);
}
