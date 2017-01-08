package io.sugo.pio.spark.engine;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface EngineFactory<TD, PD, MD, SD> {
    DataSource<TD> createDatasource();

    Preparator<TD, PD> createPreparator();

    Algorithm<PD, MD> createAlgorithm();

    Model<MD, SD> createModel();
}
