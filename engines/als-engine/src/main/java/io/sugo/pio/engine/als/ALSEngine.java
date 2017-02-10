package io.sugo.pio.engine.als;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sugo.pio.engine.als.data.ALSModelData;
import io.sugo.pio.engine.als.data.ALSPreparedData;
import io.sugo.pio.engine.als.data.ALSTrainingData;
import io.sugo.pio.engine.als.engine.ALSAlgorithm;
import io.sugo.pio.engine.als.engine.ALSDModel;
import io.sugo.pio.engine.als.engine.ALSDataSource;
import io.sugo.pio.engine.als.engine.ALSPreparator;
import io.sugo.pio.engine.data.input.BatchEventHose;
import io.sugo.pio.engine.data.output.Repository;
import io.sugo.pio.engine.training.*;

/**
 */
public class ALSEngine extends Engine<ALSTrainingData, ALSPreparedData, ALSModelData> {
    private final BatchEventHose batchEventHose;
    private final Repository repository;

    public ALSEngine(BatchEventHose batchEventHose,
                     Repository repository) {
        this.batchEventHose = batchEventHose;
        this.repository = repository;
    }
    @Override
    protected DataSource<ALSTrainingData> createDatasource() {
        return new ALSDataSource(batchEventHose);
    }

    @Override
    protected Preparator<ALSTrainingData, ALSPreparedData> createPreparator() {
        return new ALSPreparator();
    }

    @Override
    protected Algorithm<ALSPreparedData, ALSModelData> createAlgorithm() {
        return new ALSAlgorithm();
    }

    @Override
    protected Model<ALSModelData> createModel() {
        return new ALSDModel(repository);
    }
}
