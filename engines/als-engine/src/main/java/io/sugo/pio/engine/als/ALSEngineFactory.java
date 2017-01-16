package io.sugo.pio.engine.als;

import io.sugo.pio.engine.als.data.ALSModelData;
import io.sugo.pio.engine.als.data.ALSPreparedData;
import io.sugo.pio.engine.als.data.ALSTrainingData;
import io.sugo.pio.engine.als.engine.ALSAlgorithm;
import io.sugo.pio.engine.als.engine.ALSDModel;
import io.sugo.pio.engine.als.engine.ALSDataSource;
import io.sugo.pio.engine.als.engine.ALSPreparator;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.training.*;

/**
 */
public class ALSEngineFactory implements EngineFactory<ALSTrainingData, ALSPreparedData, ALSModelData> {
    private final BatchEventHose batchEventHose;

    public ALSEngineFactory(BatchEventHose batchEventHose) {
        this.batchEventHose = batchEventHose;
    }

    @Override
    public DataSource<ALSTrainingData> createDatasource() {
        return new ALSDataSource(batchEventHose);
    }

    @Override
    public Preparator<ALSTrainingData, ALSPreparedData> createPreparator() {
        return new ALSPreparator();
    }

    @Override
    public Algorithm<ALSPreparedData, ALSModelData> createAlgorithm() {
        return new ALSAlgorithm();
    }

    @Override
    public Model<ALSModelData> createModel() {
        return new ALSDModel();
    }
}
