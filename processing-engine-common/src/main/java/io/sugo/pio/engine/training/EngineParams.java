package io.sugo.pio.engine.training;

/**
 */
public interface EngineParams {
    Params getDatasourceParams();

    Params getPreparatorParams();

    Params getAlgorithmParams();
}
