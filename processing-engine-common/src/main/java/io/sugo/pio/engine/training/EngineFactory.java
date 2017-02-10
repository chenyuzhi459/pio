package io.sugo.pio.engine.training;

/**
 */
public interface EngineFactory<TD, PD, MD> {
    Engine createEngine();
}
