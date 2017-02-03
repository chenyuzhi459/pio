package io.sugo.pio.engine.training;

/**
 */
public interface EngineFactory<TD, PD, MD> {
    DataSource<TD> createDatasource();

    Preparator<TD, PD> createPreparator();

    Algorithm<PD, MD> createAlgorithm();

    Model<MD> createModel();
}
