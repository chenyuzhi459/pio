package sugo.io.pio.engine;

/**
 */
public interface EngineFactory<TD, PD, MD> {
    DataSource<TD> createDatasource();

    Preparator<PD> createPreparator();

    Algorithm<MD> createAlgorithm();
}
