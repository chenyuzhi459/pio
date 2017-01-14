package io.sugo.pio.engine.detail;

import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.input.PropertyHose;
import io.sugo.pio.engine.detail.data.DetailModelData;
import io.sugo.pio.engine.detail.data.DetailPreparedData;
import io.sugo.pio.engine.detail.data.DetailTrainingData;
import io.sugo.pio.engine.detail.engine.DetailAlgorithm;
import io.sugo.pio.engine.detail.engine.DetailDataSource;
import io.sugo.pio.engine.detail.engine.DetailModel;
import io.sugo.pio.engine.detail.engine.DetailPreparator;
import io.sugo.pio.engine.training.*;

/**
 */
public class DetailEngineFactory implements EngineFactory<DetailTrainingData, DetailPreparedData, DetailModelData> {
    private final BatchEventHose batchEventHose;

    public DetailEngineFactory(BatchEventHose batchEventHose) {
        this.batchEventHose = batchEventHose;
    }

    @Override
    public DataSource<DetailTrainingData> createDatasource() {
        return new DetailDataSource(batchEventHose);
    }

    @Override
    public Preparator<DetailTrainingData, DetailPreparedData> createPreparator() {
        return new DetailPreparator();
    }

    @Override
    public Algorithm<DetailPreparedData, DetailModelData> createAlgorithm() {
        return new DetailAlgorithm();
    }

    @Override
    public Model<DetailModelData> createModel() {
        return new DetailModel();
    }
}
