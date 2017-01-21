package io.sugo.pio.engine.prediction;

/**
 */
public class NoopQueryObject implements PredictionQueryObject {
    @Override
    public String getType() {
        return "noop";
    }
}
