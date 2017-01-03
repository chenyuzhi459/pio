package io.sugo.pio.spark;

import io.sugo.pio.spark.engine.*;

/**
 */
public class TestEngineFactory implements EngineFactory {
    @Override
    public DataSource createDatasource() {
        return null;
    }

    @Override
    public Preparator createPreparator() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return null;
    }

    @Override
    public Model createModel() {
        return null;
    }
}
