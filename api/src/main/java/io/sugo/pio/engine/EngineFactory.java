package io.sugo.pio.engine;

/**
 */
public interface EngineFactory<TD, PD, MD> {
    DataSource<TD> createDatasource();

    Preparator<TD, PD> createPreparator();

    Algorithm<PD, MD> createAlgorithm();

    Model<MD> createModel();
}
