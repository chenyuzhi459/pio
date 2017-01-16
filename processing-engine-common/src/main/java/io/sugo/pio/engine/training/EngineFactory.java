package io.sugo.pio.engine.training;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface EngineFactory<TD, PD, MD> {
    DataSource<TD> createDatasource();

    Preparator<TD, PD> createPreparator();

    Algorithm<PD, MD> createAlgorithm();

    Model<MD> createModel();
}
