package io.sugo.pio.engine.popular;

import io.sugo.pio.engine.training.EngineParams;
import io.sugo.pio.engine.training.Params;

/**
 */
public class PopEngineParams implements EngineParams {
    @Override
    public Params getDatasourceParams() {
        return null;
    }

    @Override
    public Params getPreparatorParams() {
        return null;
    }

    @Override
    public Params getAlgorithmParams() {
        return null;
    }
}